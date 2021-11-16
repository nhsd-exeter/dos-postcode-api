# Lambda

data "archive_file" "postcode_etl_sns_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/postcode_etl_sns"
  output_path = "${path.module}/functions_zip/${local.postcode_etl_sns_name}.zip"
}

data "terraform_remote_state" "vpc" {
  backend = "s3"
  config = {
    bucket = var.terraform_platform_state_store
    key    = var.vpc_terraform_state_key
    region = var.aws_region
  }
}

data "aws_security_group" "dos_application_security_group" {
  name = var.dos_security_group
}

data "aws_cloudwatch_log_group" "postcode_etl_extract_log_group" {
  name = "/aws/lambda/${local.postcode_extract_function_name}"
}

data "aws_cloudwatch_log_group" "postcode_etl_insert_log_group" {
  name = "/aws/lambda/${local.postcode_insert_function_name}"
}
