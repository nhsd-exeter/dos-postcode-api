import concurrent.futures
import itertools

import boto3
import os
import logging
import csv
import multiprocessing as mp
import logging
import csv
import os
from boto3.dynamodb.conditions import Attr, And
from concurrent.futures import ThreadPoolExecutor


s3_client = boto3.client("s3")
dynamodb = boto3.resource("dynamodb")
SOURCE_BUCKET = os.environ.get("SOURCE_BUCKET")
DYNAMODB_DESTINATION_TABLE = os.environ.get("DYNAMODB_DESTINATION_TABLE")
LOGGING_LEVEL = os.environ.get("LOGGING_LEVEL")
EMAIL_CCG_CSV_LOCATION = os.environ.get("EMAIL_CCG_CSV_KEY")


logging.basicConfig(level=LOGGING_LEVEL)
logger = logging.getLogger(__name__)


def get_file_content(LOCATION):
    return s3_client.get_object(Bucket=SOURCE_BUCKET, Key=LOCATION)["Body"].read().decode("utf-8").splitlines()


def get_file_to_csv(location):
    ccg = []
    csvreader = csv.reader(get_file_content(location))
    for row in csvreader:
        ccg.append(row)

    ccg.sort(key=lambda row: row[0])
    logger.info("Completed read of CCGs")
    return ccg


def index(array, target):
    start = 0
    end = len(array) - 1
    while start <= end:
        middle_index = (start + end) // 2
        if array[middle_index][3] == target:
            return middle_index
        elif array[middle_index][3] < target:
            start = middle_index + 1
        else:
            end = middle_index - 1
    return -1


def write_to_destination(scan_result):
    logger.info("Writing to destination")
    ccg = get_file_to_csv(CCG_CSV_LOCATION)
    dynamo_table = dynamodb.Table(DYNAMODB_DESTINATION_TABLE)
    response = "Working..."
    try:
        for scan in scan_result:
            if not scan.get("email"):
                postcode = scan["postcode"]
                nationalGroupingCode = scan["nationalGroupingCode"]
                # match against postcode
                ccg_index = index(ccg, nationalGroupingCode)
                if ccg_index >= 0:
                    item = ccg[ccg_index]
                    region = item[0]
                    icb = item[1]
                    email = item[4]
                    response = dynamo_table.update_item(
                        Key={"postcode": postcode},
                        ConditionExpression=Attr("nationalGroupingCode").eq(nationalGroupingCode),
                        UpdateExpression="set #nhs_region=:r, #icb=:i, #email=:e",
                        ExpressionAttributeNames={
                            "#nhs_region": "nhs_region",
                            "#icb": "icb",
                            "#email": "email",
                        },
                        ExpressionAttributeValues={
                            ":r": region,
                            ":i": icb,
                            ":e": email,
                        },
                        ReturnValues="UPDATED_NEW",
                    )

                    logger.info("Response for {} :{}".format(response, postcode))
        return "Response for {} :{}".format(response, postcode)
    except Exception as e:
        logger.error("An error has occurred during an update of {}: {}".format(postcode, e))
        return "{}".format(e)


def parallel_scan_table(dynamo_client, *, TableName, **kwargs):
    # How many segments to divide the table into?  As long as this is >= to the
    # number of threads used by the ThreadPoolExecutor, the exact number doesn't
    # seem to matter.
    total_segments = 100

    # How many scans to run in parallel?  If you set this really high you could
    # overwhelm the table read capacity, but otherwise I don't change this much.
    max_scans_in_parallel = 10

    # Schedule an initial scan for each segment of the table.  We read each
    # segment in a separate thread, then look to see if there are more rows to
    # read -- and if so, we schedule another scan.
    tasks_to_do = [
        {
            **kwargs,
            "TableName": TableName,
            "Segment": segment,
            "TotalSegments": total_segments,
        }
        for segment in range(total_segments)
    ]

    # Make the list an iterator, so the same tasks don't get run repeatedly.
    scans_to_run = iter(tasks_to_do)

    with concurrent.futures.ThreadPoolExecutor() as executor:

        # Schedule the initial batch of futures.  Here we assume that
        # max_scans_in_parallel < total_segments, so there's no risk that
        # the queue will throw an Empty exception.
        futures = {
            executor.submit(dynamo_client.scan, **scan_params): scan_params
            for scan_params in itertools.islice(scans_to_run, max_scans_in_parallel)
        }

        while futures:
            # Wait for the first future to complete.
            done, _ = concurrent.futures.wait(futures, return_when=concurrent.futures.FIRST_COMPLETED)

            for fut in done:
                yield from fut.result()["Items"]

                scan_params = futures.pop(fut)

                # A Scan reads up to N items, and tells you where it got to in
                # the LastEvaluatedKey.  You pass this key to the next Scan operation,
                # and it continues where it left off.
                try:
                    scan_params["ExclusiveStartKey"] = fut.result()["LastEvaluatedKey"]
                except KeyError:
                    break
                tasks_to_do.append(scan_params)

            # Schedule the next batch of futures.  At some point we might run out
            # of entries in the queue if we've finished scanning the table, so
            # we need to spot that and not throw.
            for scan_params in itertools.islice(scans_to_run, len(done)):
                futures[executor.submit(dynamo_client.scan, **scan_params)] = scan_params


def slice_array(lst, slices):
    return [lst[i::slices] for i in range(slices)]


def lambda_handler(event, context):
    logger.info("Starting email update uec-sf-ccg-email-etl")
    logger.info("Reading csv files from: " + SOURCE_BUCKET)
    logger.info("Inserting postcode data to: " + DYNAMODB_DESTINATION_TABLE)

    scans = parallel_scan_table(dynamodb.meta.client, TableName=DYNAMODB_DESTINATION_TABLE)
    chuncks = slice_array(list(scans), 10000)

    with ThreadPoolExecutor() as executor:
        results = executor.map(write_to_destination, chuncks)

    return {"statusCode": 200, "body": [*results]}
