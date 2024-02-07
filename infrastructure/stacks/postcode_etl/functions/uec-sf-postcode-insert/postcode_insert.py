from __future__ import print_function
import boto3
import os
import logging
import pandas as pd
import time
import urllib.parse
import csv
from io import StringIO


s3 = boto3.resource("s3")
dynamodb = boto3.resource("dynamodb")
SOURCE_BUCKET = os.environ.get("SOURCE_BUCKET")
INPUT_FOLDER = os.environ.get("INPUT_FOLDER")
PROCESSED_FOLDER = os.environ.get("PROCESSED_FOLDER")
SUCCESS_FOLDER = PROCESSED_FOLDER + "success/"
ERROR_FOLDER = PROCESSED_FOLDER + "error/"
DYNAMODB_DESTINATION_TABLE = os.environ.get("DYNAMODB_DESTINATION_TABLE")
LOGGING_LEVEL = os.environ.get("LOGGING_LEVEL")

logger = logging.getLogger()
logger.setLevel(LOGGING_LEVEL)
data_frame = None

# method to loop over items in s3 Bucket
def read_csv_file(bucket_name,key):
    bucket = s3.Bucket(bucket_name)
    response = dict()
    try:
        # delete the previously processed files so we have a clean workspace
        bucket.objects.filter(Prefix=PROCESSED_FOLDER).delete()
        logger.info("processing: " + key)
        s3_object = s3.Object(bucket_name, key)
        status = read_csv_file_data(s3_object)
        logger.info("read CSV data status: " + status)
        response[key] = status
        process_completed_csv_files(bucket, status, s3_object)
        logger.info("process completed: " + key)
        return response
    except Exception as e:
        logger.error("unable to retrieve csv files due to {}".format(e))
        response[key] = "FAILED {}".format(e)
        return response

def process_completed_csv_files(bucket, status, postcode_location_csv_file):
    if status.startswith("FAILED"):
        post_process_file_path = ERROR_FOLDER + postcode_location_csv_file.key.replace(INPUT_FOLDER, "")
    else:
        post_process_file_path = SUCCESS_FOLDER + postcode_location_csv_file.key.replace(INPUT_FOLDER, "")

    logger.info("moving processed file to: " + post_process_file_path)

    # copy the processed file to the relevant processed folder
    copy_source = {"Bucket": SOURCE_BUCKET, "Key": postcode_location_csv_file.key}
    s3.meta.client.copy(copy_source, SOURCE_BUCKET, post_process_file_path)

    logger.info("copied: " + postcode_location_csv_file.key + " to: " + SOURCE_BUCKET + " with path " + post_process_file_path)

    # delete the processed file so we have a clean workspace
    bucket.objects.filter(Prefix=postcode_location_csv_file.key).delete()

    logger.info("filtered: " + postcode_location_csv_file.key + " from: " + SOURCE_BUCKET)


# method to read data from a single postcode_locations csv file
def read_csv_file_data(s3_object):
    status = ""
    try:
        logger.info("reading csv file data: " + s3_object.key)
        csv_data = s3_object.get()["Body"].read().decode("utf-8")
        postcode_location_records = list()
        # Use StringIO to create a file-like object for csv.reader
        csv_file = StringIO(csv_data)

        # Parse CSV data using the csv module
        reader = csv.reader(csv_file)
        i = 0
        for row in reader:
            # Create the DynamoDB postcode_location records
            record = {}
            record["postcode"] = row[0]
            record["easting"] = int(row[1])
            record["northing"] = int(row[2])
            record["name"] = row[3] if row[3] != "" else " "
            postcode_location_records.append(record)
            i = i + 1
        insert_bulk_data(postcode_location_records)
        status = "PASSED"
        return status
    except Exception as e:
        logger.error("read csv failed due to {}".format(e))
        status = "FAILED {}".format(e)
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
            index = binary_search(data_frame, postcode)
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
        logger.info("inserted {} records into table {}".format(len(postcode_location_records), DYNAMODB_DESTINATION_TABLE))

# This is the entry point for the Lambda function
def lambda_handler(event, context):
    global data_frame
    logger.info("Start of uec-sf-postcode-insert-etl")
    cols = ['postcode','orgcode']
    logger.info("Reading csv file from data/combined.zip")
    tic = time.perf_counter()
    data_frame = pd.read_csv("./data/combined.zip", compression='zip', header=0, usecols=cols,dtype={'postcode': str, 'orgcode': str})
    logger.info("data_frame {}".format(data_frame.size))
    toc = time.perf_counter()
    logger.info(f"loaded into dataframe in  {toc - tic:0.4f} seconds")
    logger.info("Reading csv files from: " + SOURCE_BUCKET)
    logger.info("Inserting postcode data to: " + DYNAMODB_DESTINATION_TABLE)
    bucket = event['Records'][0]['s3']['bucket']['name']
    key = urllib.parse.unquote_plus(event['Records'][0]['s3']['object']['key'])
    logger.info("bucket:{},key:{}".format(bucket,key))
    response = read_csv_file(bucket,key)
    return {"statusCode": 200, "body": response}
