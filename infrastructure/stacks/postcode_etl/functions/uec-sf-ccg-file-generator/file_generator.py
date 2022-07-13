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
import io
import gzip

s3_client = boto3.client("s3")
SOURCE_BUCKET = os.environ.get("SOURCE_BUCKET")
LOGGING_LEVEL = os.environ.get("LOGGING_LEVEL")
logging.basicConfig(level=LOGGING_LEVEL)
logger = logging.getLogger(__name__)


yorkshire = "pcodey63.csv"
north_west = "pcodey62.csv"
east_england = "pcodey61.csv"
midlands = "pcodey60.csv"
south_east = "pcodey59.csv"
south_west = "pcodey58.csv"
london = "pcodey56.csv"

files = [yorkshire, north_west, east_england, midlands, south_east, south_west, london]


def main():
    with ThreadPoolExecutor(max_workers=7) as executor:
        results = executor.map(transform_pcodey_to_ccg_array, files)
        masterccg = []
        for result in results:
            masterccg.append(result)

    return write_to_s3_bucket(masterccg)


def transform_pcodey_to_ccg_array(location):
    print("Reading {}".format(location))
    ccg = get_ccg_tocsv()
    pcodey = []
    csvreader = csv.reader(get_file_content(location))

    for row in csvreader:
        index_ccg = index(ccg, row[2])
        if index_ccg >= 0:
            item_ccg = [ccg[index_ccg][1], ccg[index_ccg][2]]
            item = [row[0].replace(" ", ""), row[1], row[2], *item_ccg]
            pcodey.append(item)
    print("Completed read of {}".format(location))
    return pcodey


def get_ccg_tocsv():
    ccg_csv = []
    ECCG_LOCATION = "eccg.csv"
    csvreader = csv.reader(get_file_content(ECCG_LOCATION))
    for row in csvreader:
        ccg_csv.append(row)
        ccg_csv.sort(key=lambda row: row[0])
    print("Completed read of CCGs")
    return ccg_csv


def index(array, target):
    array.sort(key=lambda x: x[0])
    start = 0
    end = len(array) - 1
    while start <= end:
        middle_index = (start + end) // 2
        if array[middle_index][0] == target:
            return middle_index
        elif array[middle_index][0] < target:
            start = middle_index + 1
        else:
            end = middle_index - 1
    return -1


def write_to_s3_bucket(array):
    print("Writing to file")
    try:
        csv_buffer = io.StringIO()
        writer = csv.writer(csv_buffer)
        for arr in array:
            for element in arr:
                writer.writerow(element)

        csv_buffer_to_binary = io.BytesIO(csv_buffer.getvalue().encode("utf-8"))
        logger.info("Saving file to master_ccg_file.csv")
        s3_client.put_object(Bucket=SOURCE_BUCKET, Key="master_ccg_file.csv", Body=csv_buffer_to_binary)
        return "Saving file to master_ccg_file.csv"
    except Exception as e:
        logger.error("An error whilst generating master ccg file: {}".format(e))


def get_file_content(LOCATION):
    return s3_client.get_object(Bucket=SOURCE_BUCKET, Key=LOCATION)["Body"].read().decode("utf-8").splitlines()


# This is the entry point for the Lambda function
def lambda_handler(event, context):

    logger.info("Start of ccg file generator")
    fileCount = main()

    return {"statusCode": 200, "body": fileCount}
