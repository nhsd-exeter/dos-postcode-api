from __future__ import print_function
import boto3
import base64
from botocore.exceptions import ClientError
import psycopg2
import psycopg2.extras
import os
import json
import logging
import csv
import io

s3 = boto3.resource(u"s3")
SOURCE_BUCKET = os.environ.get("SOURCE_BUCKET")
SOURCE_FOLDER = os.environ.get("SOURCE_FOLDER")
FILE_PREFIX = os.environ.get("FILE_PREFIX")
USR = os.environ.get("USR")
SOURCE_DB = os.environ.get("SOURCE_DB")
ENDPOINT = os.environ.get("ENDPOINT").split(":")[0]
PORT = os.environ.get("PORT")
REGION = os.environ.get("REGION")
BATCH_SIZE = int(os.environ.get("BATCH_SIZE"))
SECRET_NAME = os.environ.get("SECRET_NAME")
DOS_READ_ONLY_USER = os.environ.get("DOS_READ_ONLY_USER")
LOGGING_LEVEL = os.environ.get("LOGGING_LEVEL")

logging.basicConfig(level=LOGGING_LEVEL)
logger=logging.getLogger(__name__)

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
                                od."name"  as org_name,
                                od.organisationtypeid as organisationtypeid
                            from pathwaysdos.locations l
                                left outer join (SELECT DISTINCT ON (postcode) o.id, postcode ,o.orgcode ,org."name" ,org.organisationtypeid
                                FROM pathwaysdos.odspostcodes o
                                left outer join pathwaysdos.organisations org on org.code = o.orgcode
                                where org.organisationtypeid =1 and o.deletedtime is null
                                ORDER BY postcode, o.id DESC) od on l.postcode = od.postcode) as pl"""
    logger.info("Open connection")
    conn = connect()
    logger.info("Connection opened")
    cur = get_cursor(conn)
    try:
        cur.execute(select_statement)
        count = 0
        while True:
            records = cur.fetchmany()
            count = count + 1
            if not records:
                break
            save_to_csv(records,count)
        return count
    except Exception as e:
        logger.error("Extract postcode failed due to {}".format(e))
        raise e
    finally:
        cur.close()
        conn.close()
        logger.info("PostgreSQL connection is closed")


def save_to_csv(query_results, count):
    str_count = str(count)
    file_name = SOURCE_FOLDER + FILE_PREFIX + str_count + ".csv"
    try:
        csv_buffer = io.StringIO()
        writer = csv.writer(csv_buffer)

        counter = 0
        for row in query_results:
            writer.writerow(row)
            counter = counter + 1
            if counter == len(query_results):
                break

        # Prepare buffer and transform to binary
        csv_buffer_to_binary = io.BytesIO(csv_buffer.getvalue().encode("utf-8"))

        # save to s3 bucket
        logger.info("Saving file to: " + file_name)
        bucket = s3.Bucket(SOURCE_BUCKET)
        bucket.put_object(Key=file_name, Body=csv_buffer_to_binary)
    except Exception as e:
        logger.error("Failed to create file in s3 bucket due to {}".format(e))
        raise e

# This is the entry point for the Lambda function
def lambda_handler(event, context):
    logger.info("Start of postcode_extract")
    file_count = extract_postcodes()
    logger.info("loaded  csv files successfully.. Start of postcode_extract")
    return {"statusCode": 200, "body": str(file_count) + " file(s) created in s3 bucket"}
