from __future__ import print_function
import boto3
import os
import logging
import pandas as pd
import glob
import time
s3 = boto3.resource("s3")
dynamodb = boto3.resource("dynamodb")
SOURCE_BUCKET = os.environ.get("SOURCE_BUCKET")
INPUT_FOLDER = os.environ.get("INPUT_FOLDER")
PROCESSED_FOLDER = os.environ.get("PROCESSED_FOLDER")
SUCCESS_FOLDER = PROCESSED_FOLDER + "success/"
ERROR_FOLDER = PROCESSED_FOLDER + "error/"
DYNAMODB_DESTINATION_TABLE = os.environ.get("DYNAMODB_DESTINATION_TABLE")
LOGGING_LEVEL = os.environ.get("LOGGING_LEVEL")

logging.basicConfig(level=LOGGING_LEVEL)
logger = logging.getLogger(__name__)
data_frame = None

# method to loop over items in s3 Bucket
def read_csv_files():
    bucket = s3.Bucket(SOURCE_BUCKET)
    response = dict()

    try:
        # delete the previously processed files so we have a clean workspace
        bucket.objects.filter(Prefix=PROCESSED_FOLDER).delete()

        postcode_location_csv_files = bucket.objects.filter(Prefix=INPUT_FOLDER, Delimiter="/")

        for postcode_location_csv_file in postcode_location_csv_files:
            name = postcode_location_csv_file.key
            if name != INPUT_FOLDER:
                print("processing: " + name)
                status = read_csv_file_data(postcode_location_csv_file.get())
                print("read CSV data status: " + status)
                response[name] = status
                process_completed_csv_files(bucket, status, postcode_location_csv_file)
                print("process completed: " + name)

    except Exception as e:
        logger.error("unable to retrieve csv files due to {}".format(e))
    finally:
        return response


def process_completed_csv_files(bucket, status, postcode_location_csv_file):
    if status.startswith("FAILED"):
        post_process_file_path = ERROR_FOLDER + postcode_location_csv_file.key.replace(INPUT_FOLDER, "")
    else:
        post_process_file_path = SUCCESS_FOLDER + postcode_location_csv_file.key.replace(INPUT_FOLDER, "")

    print("moving processed file to: " + post_process_file_path)

    # copy the processed file to the relevant processed folder
    copy_source = {"Bucket": SOURCE_BUCKET, "Key": postcode_location_csv_file.key}
    s3.meta.client.copy(copy_source, SOURCE_BUCKET, post_process_file_path)

    print("copied: " + postcode_location_csv_file.key + " to: " + SOURCE_BUCKET + " with path " + post_process_file_path)

    # delete the processed file so we have a clean workspace
    bucket.objects.filter(Prefix=postcode_location_csv_file.key).delete()

    print("filtered: " + postcode_location_csv_file.key + " from: " + SOURCE_BUCKET)


# method to read data from a single postcode_locations csv file
def read_csv_file_data(postcode_location_csv_file):
    status = ""
    try:
        postcode_location_entries = postcode_location_csv_file["Body"].read().splitlines()
        postcode_location_records = list()

        i = 0
        while i < len(postcode_location_entries):
            # Now split the entries into an array of postcode location attributes
            postcode_location_entry = postcode_location_entries[i]
            postcode_location_attributes = postcode_location_entry.decode().split(",")

            # Create the DynamoDB postcode_location records
            record = {}
            record["postcode"] = postcode_location_attributes[0]
            record["easting"] = int(postcode_location_attributes[1])
            record["northing"] = int(postcode_location_attributes[2])
            record["name"] = postcode_location_attributes[3] if postcode_location_attributes[3] != "" else " "
            postcode_location_records.append(record)

            i = i + 1

        insert_bulk_data(postcode_location_records)
        status = "PASSED"
    except Exception as e:
        logger.error("read csv failed due to {}".format(e))
        status = "FAILED {}".format(e)
    finally:
        return status

# binary search method to find a postcode in the dataframe
def binary_search(df, search_value):
    left, right = 0, len(df) - 1

    while left <= right:
        mid = (left + right) // 2
        mid_value = df.iloc[mid]['postcode']

        if mid_value == search_value:
            return df.iloc[mid]  # Found the value
        elif mid_value < search_value:
            left = mid + 1
        else:
            right = mid - 1
    return None  # Value not found

# method to insert bulk data into dynamoDB. The batch_writer will automatically batch up the inserts for
# us, reducting the number of insert calls we need to make.
def insert_bulk_data(postcode_location_records):
    table = dynamodb.Table(DYNAMODB_DESTINATION_TABLE)

    with table.batch_writer(overwrite_by_pkeys=["postcode", "name"]) as batch:
        for i in range(len(postcode_location_records)):
            postcode_location = postcode_location_records[i]
            postcode = postcode_location["postcode"].replace(" ", "")
            index = binary_search(data_frame, postcode_location["postcode"])
            orgcode = index["orgcode"] if index is not None else ""
            batch.put_item(
                Item={
                    "postcode": postcode,
                    "easting": postcode_location["easting"],
                    "northing": postcode_location["northing"],
                    "name": postcode_location["name"],
                    "orgcode": orgcode
                }
            )
        print("inserted {} records into table {}".format(len(postcode_location_records), DYNAMODB_DESTINATION_TABLE))

# This is the entry point for the Lambda function
def lambda_handler(event, context):
    global data_frame
    logger.info("Start of uec-sf-postcode-insert-etl")
    cols = ['postcode','orgcode']
    logger.info("Reading csv file from data/combined.zip")
    tic = time.perf_counter()
    data_frame = pd.read_csv("./data/combined.zip", compression='zip', header=0, usecols=cols,dtype={'postcode': str, 'orgcode': str})
    toc = time.perf_counter()
    logger.info(f"loaded into dataframe in  {toc - tic:0.4f} seconds")
    logger.info("Reading csv files from: " + SOURCE_BUCKET)
    logger.info("Inserting postcode data to: " + DYNAMODB_DESTINATION_TABLE)
    response = read_csv_files()
    return {"statusCode": 200, "body": response}
