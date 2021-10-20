
resource "aws_lambda_function" "ccg_insert_lambda" {
  filename         = data.archive_file.ccg_insert_function.output_path
  function_name    = local.ccg_insert_function_name
  description      = local.ccg_insert_description
  role             = aws_iam_role.ccg_insert_lambda_role.arn
  handler          = "ccg_insert.lambda_handler"
  source_code_hash = data.archive_file.ccg_insert_function.output_base64sha256
  runtime          = local.ccg_insert_runtime
  timeout          = local.ccg_insert_timeout
  memory_size      = local.ccg_insert_memory_size
  publish          = true
  tags             = local.standard_tags
  environment {
    variables = {
      SOURCE_BUCKET              = local.ccg_etl_s3_bucket
      DYNAMODB_DESTINATION_TABLE = local.ccg_insert_dynamoDb_destination_table
      INPUT_FOLDER               = local.ccg_etl_s3_source_folder
      PROCESSED_FOLDER           = local.ccg_etl_s3_processed_folder
    }
  }
}

resource "aws_iam_role" "ccg_insert_lambda_role" {
  name               = local.ccg_insert_iam_name
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

resource "aws_iam_role_policy_attachment" "s3FullAccessInsert" {
  role       = aws_iam_role.ccg_insert_lambda_role.name
  policy_arn = local.s3_full_access_policy_arn
}

resource "aws_iam_role_policy_attachment" "dynamoDbFullAccessInsert" {
  role       = aws_iam_role.ccg_insert_lambda_role.name
  policy_arn = local.dynamoDb_full_access_policy_arn
}

resource "aws_cloudwatch_event_rule" "ccg_insert_cloudwatch_event" {
  name                = local.ccg_insert_cloudwatch_event_name
  description         = local.ccg_insert_cloudwatch_event_description
  schedule_expression = local.ccg_insert_cloudwatch_event_cron_expression
}

resource "aws_cloudwatch_event_target" "daily_ccg_insert_job" {
  rule      = aws_cloudwatch_event_rule.ccg_insert_cloudwatch_event.name
  target_id = local.ccg_insert_cloudwatch_event_target
  arn       = aws_lambda_function.ccg_insert_lambda.arn
}

resource "aws_lambda_permission" "allow_cloudwatch_to_call_insert_ccg" {
  statement_id  = local.ccg_insert_cloudwatch_event_statement
  action        = local.ccg_insert_cloudwatch_event_action
  function_name = local.ccg_insert_function_name
  principal     = local.ccg_insert_cloudwatch_event_princinple
  source_arn    = aws_cloudwatch_event_rule.ccg_insert_cloudwatch_event.arn
}
