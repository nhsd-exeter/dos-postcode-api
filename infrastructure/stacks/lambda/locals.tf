locals {

  ccg_insert_function_name = "${var.service_prefix}-ccg-insert"
  ccg_insert_description   = "Service Finder function to insert ccg and postcode mappings into DynamoDB"
  ccg_insert_runtime       = "python3.6"
  ccg_insert_timeout       = 900
  ccg_insert_memory_size   = 1024


  ccg_extract_function_name            = "${var.service_prefix}-ccg-extract"
  ccg_extract_description              = "Service Finder function to extract postcode and ccg mapping from DoS database into csv files"
  ccg_extract_runtime                  = "python3.8"
  ccg_extract_timeout                  = 300
  ccg_extract_memory_size              = 1024
  ccg_extract_core_dos_python_libs_arn = data.aws_lambda_layer_version.dos_python_libs.arn

  ccg_extract_vpc_security_group = data.aws_security_group.dos_application_security_group.id

  //Environment variables for lambda
  ccg_insert_dynamoDb_destination_table = var.postcode_mapping_dynamo_name
  ccg_etl_s3_bucket                     = var.sf_resources_bucket
  ccg_etl_s3_bucket_arn                 = data.aws_s3_bucket.s3_bucket.arn
  ccg_etl_s3_source_folder              = "postcode_locations/"
  ccg_etl_s3_file_prefix                = "postcode_extract"
  ccg_etl_s3_processed_folder           = "processed_ccgs/"
  ccg_extract_db_user                   = var.ccg_etl_db_user
  ccg_extract_source_db                 = var.ccg_etl_source_db
  ccg_extract_db_endpoint               = var.dos_replica_db
  ccg_extract_db_port                   = "5432"
  ccg_extract_db_region                 = "eu-west-2"
  ccg_extract_db_batch_size             = "100000"
  ccg_extract_db_secret_name            = var.dos_read_replica_secret_name
  ccg_extract_db_key                    = var.dos_read_replica_key
  ccg_extract_db_secret_arn             = data.aws_secretsmanager_secret.dos_read_replica_secret_name.arn

  ccg_insert_iam_name = "${var.service_prefix}-ccg-insert-lambda"

  ccg_extract_iam_name    = "${var.service_prefix}-ccg-extract-lambda"
  ccg_extract_policy_name = "${var.service_prefix}-ccg-dos-extract"

  s3_full_access_policy_arn            = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
  dynamoDb_full_access_policy_arn      = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
  rds_data_read_only_access_policy_arn = "arn:aws:iam::aws:policy/AmazonRDSReadOnlyAccess"

  ccg_insert_cloudwatch_event_name            = "${var.service_prefix}-ccg-insert-rule"
  ccg_insert_cloudwatch_event_description     = "Daily timer to run in the ccg csv files into the dynamoDB at 2am"
  ccg_insert_cloudwatch_event_cron_expression = "cron(0 2 ? * * *)"
  ccg_insert_cloudwatch_event_target          = "lambda"
  ccg_insert_cloudwatch_event_statement       = "AllowExecutionFromCloudWatch"
  ccg_insert_cloudwatch_event_action          = "lambda:InvokeFunction"
  ccg_insert_cloudwatch_event_princinple      = "events.amazonaws.com"

  ccg_extract_cloudwatch_event_name            = "${var.service_prefix}-ccg-extract-rule"
  ccg_extract_cloudwatch_event_description     = "Daily timer to extract ccg and postcode data out of DoS an into the s3 bucket at 1am"
  ccg_extract_cloudwatch_event_cron_expression = "cron(0 1 ? * * *)"
  ccg_extract_cloudwatch_event_target          = "lambda"
  ccg_extract_cloudwatch_event_statement       = "AllowExecutionFromCloudWatch"
  ccg_extract_cloudwatch_event_action          = "lambda:InvokeFunction"
  ccg_extract_cloudwatch_event_princinple      = "events.amazonaws.com"

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "dos-api"
    "Product"     = "postcode"
    "Environment" = var.profile
  }

}
