# Lambda
data "archive_file" "postcode_insert_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/uec-sf-postcode-insert"
  output_path = "${path.module}/functions_zip/${local.postcode_insert_function_name}.zip"
}

data "archive_file" "postcode_extract_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/uec-sf-postcode-extract"
  output_path = "${path.module}/functions_zip/${local.postcode_extract_function_name}.zip"
}

data "archive_file" "region_update_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/uec-sf-region-update"
  output_path = "${path.module}/functions_zip/${local.region_update_function_name}.zip"
}

data "archive_file" "email_update_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/uec-sf-email-update"
  output_path = "${path.module}/functions_zip/${local.email_update_function_name}.zip"
}
data "archive_file" "file_generator_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/uec-sf-ccg-file-generator"
  output_path = "${path.module}/functions_zip/${var.service_prefix}-ccg-file-generator.zip"
}

data "terraform_remote_state" "vpc" {
  backend = "s3"
  config = {
    bucket = var.terraform_platform_state_store
    key    = var.vpc_terraform_state_key
    region = var.aws_region
  }
}

data "aws_security_group" "sf_read_replica_db_sg" {
  name = var.sf_read_replica_db_sg
}

# data "aws_lambda_layer_version" "dos_python_libs" {
#   layer_name = var.core_dos_python_libs
# }

data "aws_secretsmanager_secret" "dos_read_replica_secret_name" {
  name = var.dos_read_replica_secret_name
}
