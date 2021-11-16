resource "aws_lambda_function" "postcode_extract_lambda" {
  filename         = data.archive_file.postcode_extract_function.output_path
  function_name    = local.postcode_extract_function_name
  description      = local.postcode_extract_description
  role             = aws_iam_role.postcode_extract_lambda_role.arn
  handler          = "postcode_extract.lambda_handler"
  source_code_hash = data.archive_file.postcode_extract_function.output_base64sha256
  runtime          = local.postcode_extract_runtime
  timeout          = local.postcode_extract_timeout
  memory_size      = local.postcode_extract_memory_size
  publish          = true
  tags             = local.standard_tags
  layers           = [local.postcode_extract_core_dos_python_libs_arn]
  environment {
    variables = {
      SOURCE_BUCKET      = local.postcode_etl_s3_bucket
      SOURCE_FOLDER      = local.postcode_etl_s3_source_folder
      FILE_PREFIX        = local.postcode_etl_s3_file_prefix
      USR                = local.postcode_extract_db_user
      SOURCE_DB          = local.postcode_extract_source_db
      ENDPOINT           = local.postcode_extract_db_endpoint
      PORT               = local.postcode_extract_db_port
      REGION             = local.postcode_extract_db_region
      BATCH_SIZE         = local.postcode_extract_db_batch_size
      SECRET_NAME        = local.postcode_extract_db_secret_name
      DOS_READ_ONLY_USER = local.postcode_extract_db_key
      LOGGING_LEVEL      = var.postcode_etl_logging_level
    }
  }
  vpc_config {
    subnet_ids = [
      data.terraform_remote_state.vpc.outputs.private_subnets[0],
      data.terraform_remote_state.vpc.outputs.private_subnets[1],
      data.terraform_remote_state.vpc.outputs.private_subnets[2]
    ]
    security_group_ids = [local.postcode_extract_vpc_security_group]
  }
}
resource "aws_iam_role" "postcode_extract_lambda_role" {
  name               = local.postcode_extract_iam_name
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

resource "aws_iam_role_policy" "uec-sf-postcode-dos-extract" {
  name   = local.postcode_extract_policy_name
  role   = aws_iam_role.postcode_extract_lambda_role.name
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
      "Resource": "${local.postcode_extract_db_secret_arn}"
    },
    {
      "Effect": "Allow",
      "Action": ["s3:PutObject*"],
      "Resource": "${local.postcode_etl_s3_bucket_arn}/*"
    },
    {
      "Effect": "Allow",
      "Action": [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
      ],
      "Resource": "*"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "AWSLambdaVPCAccessExecutionRole" {
  role       = aws_iam_role.postcode_extract_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "rdsDataReadOnlyAccessExtract" {
  role       = aws_iam_role.postcode_extract_lambda_role.name
  policy_arn = local.rds_data_read_only_access_policy_arn
}

resource "aws_cloudwatch_event_rule" "postcode_extract_cloudwatch_event" {
  name                = local.postcode_extract_cloudwatch_event_name
  description         = local.postcode_extract_cloudwatch_event_description
  schedule_expression = local.postcode_extract_cloudwatch_event_cron_expression
}

resource "aws_cloudwatch_event_target" "daily_postcode_extract_job" {
  rule      = aws_cloudwatch_event_rule.postcode_extract_cloudwatch_event.name
  target_id = local.postcode_extract_cloudwatch_event_target
  arn       = aws_lambda_function.postcode_extract_lambda.arn
}

resource "aws_lambda_permission" "allow_cloudwatch_to_call_extract_postcode" {
  statement_id  = local.postcode_extract_cloudwatch_event_statement
  action        = local.postcode_extract_cloudwatch_event_action
  function_name = local.postcode_extract_function_name
  principal     = local.postcode_extract_cloudwatch_event_princinple
  source_arn    = aws_cloudwatch_event_rule.postcode_extract_cloudwatch_event.arn
}

resource "aws_cloudwatch_log_group" "postcode_extract_log_group" {
  name = "/aws/lambda/${local.postcode_extract_function_name}"
}
