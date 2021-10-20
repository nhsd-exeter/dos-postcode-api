resource "aws_lambda_function" "ccg_extract_lambda" {
  filename         = data.archive_file.ccg_extract_function.output_path
  function_name    = local.ccg_extract_function_name
  description      = local.ccg_extract_description
  role             = aws_iam_role.ccg_extract_lambda_role.arn
  handler          = "ccg_extract.lambda_handler"
  source_code_hash = data.archive_file.ccg_extract_function.output_base64sha256
  runtime          = local.ccg_extract_runtime
  timeout          = local.ccg_extract_timeout
  memory_size      = local.ccg_extract_memory_size
  publish          = true
  tags             = local.standard_tags
  layers           = [local.ccg_extract_core_dos_python_libs_arn]
  environment {
    variables = {
      SOURCE_BUCKET = local.ccg_etl_s3_bucket
      SOURCE_FOLDER = local.ccg_etl_s3_source_folder
      FILE_PREFIX   = local.ccg_etl_s3_file_prefix
      USR           = local.ccg_extract_db_user
      SOURCE_DB     = local.ccg_extract_source_db
      ENDPOINT      = local.ccg_extract_db_endpoint
      PORT          = local.ccg_extract_db_port
      REGION        = local.ccg_extract_db_region
      BATCH_SIZE    = local.ccg_extract_db_batch_size
      SECRET_NAME   = local.ccg_extract_db_secret_name
      KEY           = local.ccg_extract_db_key
    }
  }
  vpc_config {
    subnet_ids = [
      data.terraform_remote_state.vpc.outputs.private_subnets[0],
      data.terraform_remote_state.vpc.outputs.private_subnets[1],
      data.terraform_remote_state.vpc.outputs.private_subnets[2]
    ]
    security_group_ids = [local.ccg_extract_vpc_security_group]
  }
}
resource "aws_iam_role" "ccg_extract_lambda_role" {
  name               = local.ccg_extract_iam_name
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "uec-sf-ccg-dos-extract" {
  name   = local.ccg_extract_policy_name
  role   = aws_iam_role.ccg_extract_lambda_role.name
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:Describe*",
        "secretsmanager:Get*",
        "secretsmanager:List*"
      ],
      "Resource": "${local.ccg_extract_db_secret_arn}"
    },
    {
      "Effect": "Allow",
      "Action": ["s3:PutObject*"],
      "Resource": "${local.ccg_etl_s3_bucket_arn}/*"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "AWSLambdaVPCAccessExecutionRole" {
  role       = aws_iam_role.ccg_extract_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "rdsDataReadOnlyAccessExtract" {
  role       = aws_iam_role.ccg_extract_lambda_role.name
  policy_arn = local.rds_data_read_only_access_policy_arn
}

resource "aws_cloudwatch_event_rule" "ccg_extract_cloudwatch_event" {
  name                = local.ccg_extract_cloudwatch_event_name
  description         = local.ccg_extract_cloudwatch_event_description
  schedule_expression = local.ccg_extract_cloudwatch_event_cron_expression
}

resource "aws_cloudwatch_event_target" "daily_ccg_extract_job" {
  rule      = aws_cloudwatch_event_rule.ccg_extract_cloudwatch_event.name
  target_id = local.ccg_extract_cloudwatch_event_target
  arn       = aws_lambda_function.ccg_extract_lambda.arn
}

resource "aws_lambda_permission" "allow_cloudwatch_to_call_extract_ccg" {
  statement_id  = local.ccg_extract_cloudwatch_event_statement
  action        = local.ccg_extract_cloudwatch_event_action
  function_name = local.ccg_extract_function_name
  principal     = local.ccg_extract_cloudwatch_event_princinple
  source_arn    = aws_cloudwatch_event_rule.ccg_extract_cloudwatch_event.arn
}
