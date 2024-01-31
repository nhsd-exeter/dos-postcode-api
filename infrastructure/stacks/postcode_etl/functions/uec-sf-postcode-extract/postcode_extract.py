from __future__ import print_function
import boto3
import base64
from botocore.exceptions import ClientError
import psycopg2
import psycopg2.extras
import os
import json
import logging
import pandas as pd
import glob
from pandas import DataFrame

s3 = boto3.resource(u"s3")
dynamodb = boto3.resource("dynamodb")

USR = os.environ.get("USR")
SOURCE_DB = os.environ.get("SOURCE_DB")
ENDPOINT = os.environ.get("ENDPOINT").split(":")[0]
PORT = os.environ.get("PORT")
REGION = os.environ.get("REGION")
BATCH_SIZE = int(os.environ.get("BATCH_SIZE"))
SECRET_NAME = os.environ.get("SECRET_NAME")
DOS_READ_ONLY_USER = os.environ.get("DOS_READ_ONLY_USER")
LOGGING_LEVEL = os.environ.get("LOGGING_LEVEL")
DYNAMODB_DESTINATION_TABLE = os.environ.get("DYNAMODB_DESTINATION_TABLE")



logger = logging.getLogger()
logger.setLevel(LOGGING_LEVEL)
combined_df = DataFrame()

def get_secret():

    secret_name = SECRET_NAME
    region_name = "eu-west-2"

    # Create a Secrets Manager client
    session = boto3.session.Session()
    client = session.client(service_name="secretsmanager", region_name=region_name)

    # In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
    # See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
    # We rethrow the exception by default.

    try:
        get_secret_value_response = client.get_secret_value(SecretId=secret_name)
    except ClientError as e:
        if e.response["Error"]["Code"] == "DecryptionFailureException":
            # Secrets Manager can't decrypt the protected secret text using the provided KMS key.
            # Deal with the exception here, and/or rethrow at your discretion.
            raise e
        elif e.response["Error"]["Code"] == "InternalServiceErrorException":
            # An error occurred on the server side.
            # Deal with the exception here, and/or rethrow at your discretion.
            raise e
        elif e.response["Error"]["Code"] == "InvalidParameterException":
            # You provided an invalid value for a parameter.
            # Deal with the exception here, and/or rethrow at your discretion.
            raise e
        elif e.response["Error"]["Code"] == "InvalidRequestException":
            # You provided a parameter value that is not valid for the current state of the resource.
            # Deal with the exception here, and/or rethrow at your discretion.
            raise e
        elif e.response["Error"]["Code"] == "ResourceNotFoundException":
            # We can't find the resource that you asked for.
            # Deal with the exception here, and/or rethrow at your discretion.
            raise e
    else:
        # Decrypts secret using the associated KMS CMK.
        # Depending on whether the secret is a string or binary, one of these fields will be populated.
        if "SecretString" in get_secret_value_response:
            return get_secret_value_response["SecretString"]
        else:
            return base64.b64decode(get_secret_value_response["SecretBinary"])


# Method to connect to database
def connect():
    try:
        secret_dict = json.loads(get_secret())
        conn = psycopg2.connect(
            host=ENDPOINT,
            port=PORT,
            user=USR,
            database=SOURCE_DB,
            password=secret_dict[DOS_READ_ONLY_USER],
        )
        return conn
    except Exception as e:
        logger.error("Database connection failed due to {}".format(e))
        raise e


# Method to get cursor from db
def get_cursor(conn):
    try:
        cur = conn.cursor("sf-postcode-extract-odspostcodes")
        cur.itersize = BATCH_SIZE
        cur.arraysize = BATCH_SIZE
        logger.info("Created cursor and set the batch size to  {}".format(BATCH_SIZE))
        return cur
    except Exception as e:
        logger.error("unable to retrieve cursor due to {}".format(e))
        conn.close()
        raise e


# Method to extract postcodes from db
def extract_postcodes():

    select_statement = """select
                            pl.postcode,
                            pl.easting,
                            pl.northing,
                            pl.org_name
                        from
                            (select l.postcode as postcode,
                                l.easting as easting,
                                l.northing as northing,
                                org."name" as org_name,
                                org.organisationtypeid as organisationtypeid
                            from pathwaysdos.locations l
                                left outer join pathwaysdos.odspostcodes o on l.postcode = o.postcode
                                left outer join pathwaysdos.organisations org on org.code = o.orgcode
                            where o.deletedtime is null) as pl
                        where (pl.organisationtypeid = 1 or pl.org_name is null)"""

    logger.info("Open connection")
    conn = connect()
    logger.info("Connection opened")
    cur = get_cursor(conn)
    try:
        cur.execute(select_statement)
        count = 0
        while True:
            records = cur.fetchmany()
            count = count + len(records)
            if not records:
                break
            insert_bulk_data(records)
        return count
    except Exception as e:
        logger.error("Extract postcode failed due to {}".format(e))
        raise e
    finally:
        cur.close()
        conn.close()
        logger.info("PostgreSQL connection is closed")

def insert_bulk_data(postcode_location_records):
    table = dynamodb.Table(DYNAMODB_DESTINATION_TABLE)

    with table.batch_writer(overwrite_by_pkeys=["postcode", "name"]) as batch:
        for postcode_location in postcode_location_records:
            postcode = postcode_location[0].replace(" ", "")
            batch.put_item(
                Item={
                    "postcode": postcode,
                    "easting": postcode_location[1],
                    "northing": postcode_location[2],
                    "name": postcode_location[3],
                    "orgcode": get_orgcode(postcode)
                }
            )
        logger.info("inserted {} records into table {}".format(len(postcode_location_records), DYNAMODB_DESTINATION_TABLE))
def get_orgcode(postcode):
    result = combined_df[combined_df['postcode'] == postcode]['orgcode']
    if (result.empty):
        return ""
    return result.values[0]
# This is the entry point for the Lambda function
def lambda_handler(event, context):
    csv_files_path = "/opt/data/pcodey*.csv"
    csv_files = glob.glob(csv_files_path)
    data_frames = []
    for csv_file in csv_files:
        df = pd.read_csv(csv_file, header=0)
        data_frames.append(df)
    combined_df = pd.concat(data_frames, ignore_index=True)
    logger.info("loaded  csv files successfully.. Start of postcode_extract")
    try:
        records_count = extract_postcodes()
        return {"statusCode": 200, "body": str(records_count) + " records updated successfully"}
    except Exception as e:
        return {"statusCode": 500, "body": "Failed to update records due to {}".format(e)}
