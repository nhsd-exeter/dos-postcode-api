from __future__ import print_function
import boto3
import os
import logging

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


# method to loop over items in s3 Bucket
def readCsvFiles():
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
                status = readCsvFileData(postcode_location_csv_file.get())
                print("read CSV data status: " + status)
                response[name] = status
                processCompletedCsvFiles(bucket, status, postcode_location_csv_file)
                print("process completed: " + name)

    except Exception as e:
        logger.error("unable to retrieve csv files due to {}".format(e))
    finally:
        return response


def processCompletedCsvFiles(bucket, status, postcode_location_csv_file):
    if status.startswith("FAILED"):
        postProcessFilePath = ERROR_FOLDER + postcode_location_csv_file.key.replace(INPUT_FOLDER, "")
    else:
        postProcessFilePath = SUCCESS_FOLDER + postcode_location_csv_file.key.replace(INPUT_FOLDER, "")

    print("moving processed file to: " + postProcessFilePath)

    # copy the processed file to the relevant processed folder
    copy_source = {"Bucket": SOURCE_BUCKET, "Key": postcode_location_csv_file.key}
    s3.meta.client.copy(copy_source, SOURCE_BUCKET, postProcessFilePath)

    print("copied: " + postcode_location_csv_file.key + " to: " + SOURCE_BUCKET + " with path " + postProcessFilePath)

    # delete the processed file so we have a clean workspace
    bucket.objects.filter(Prefix=postcode_location_csv_file.key).delete()

    print("filtered: " + postcode_location_csv_file.key + " from: " + SOURCE_BUCKET)


# method to read data from a single postcode_locations csv file
def readCsvFileData(postcode_location_csv_file):
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
            record["orgcode"] = postcode_location_attributes[4] if postcode_location_attributes[4] != "" else " "


            postcode_location_records.append(record)

            i = i + 1

        insert_bulk_data(postcode_location_records)
        status = "PASSED"
    except Exception as e:
        logger.error("read csv failed due to {}".format(e))
        status = "FAILED {}".format(e)
    finally:
        return status


# method to insert bulk data into dynamoDB. The batch_writer will automatically batch up the inserts for
# us, reducting the number of insert calls we need to make.
def insert_bulk_data(postcode_location_records):
    table = dynamodb.Table(DYNAMODB_DESTINATION_TABLE)

    with table.batch_writer(overwrite_by_pkeys=["postcode", "name"]) as batch:
        for i in range(len(postcode_location_records)):
            postcode_location = postcode_location_records[i]
            batch.put_item(
                Item={
                    "postcode": postcode_location["postcode"].replace(" ", ""),
                    "easting": postcode_location["easting"],
                    "northing": postcode_location["northing"],
                    "name": postcode_location["name"],
                    "orgcode": postcode_location["orgcode"],

                }
            )
        print("inserted {} records into table {}".format(len(postcode_location_records), DYNAMODB_DESTINATION_TABLE))

# This is the entry point for the Lambda function
def lambda_handler(event, context):

    logger.info("Start of uec-sf-postcode-location-etl")
    logger.info("Reading csv files from: " + SOURCE_BUCKET)
    logger.info("Inserting postcode data to: " + DYNAMODB_DESTINATION_TABLE)

    response = readCsvFiles()

    return {"statusCode": 200, "body": response}
