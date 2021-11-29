resource "aws_sns_topic" "postcode_etl_sns_topic" {
  name = local.postcode_etl_sns_name
}

resource "aws_sns_topic_subscription" "postcode_etl_sns_subscription" {
  topic_arn = aws_sns_topic.postcode_etl_sns_topic.arn
  protocol  = "email"
  endpoint  = var.postcode_etl_sns_email
}



resource "aws_iam_role" "postcode_etl_sns_role" {
  name               = local.postcode_etl_sns_name
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

resource "aws_iam_role_policy" "uec-postcode_etl_sns_role_policy" {
  name   = local.postcode_etl_sns_policy_name
  role   = aws_iam_role.postcode_etl_sns_role.name
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": "sns:Publish",
            "Resource": "${aws_sns_topic.postcode_etl_sns_topic.arn}"
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

resource "aws_iam_role_policy_attachment" "AWSLambdaVPCAccessExecutionRoleForSns" {
  role       = aws_iam_role.postcode_etl_sns_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_lambda_function" "postcode_etl_sns_lambda" {
  filename         = data.archive_file.postcode_etl_sns_function.output_path
  function_name    = local.postcode_etl_sns_name
  description      = local.postcode_etl_sns_description
  role             = aws_iam_role.postcode_etl_sns_role.arn
  handler          = "postcode_etl_sns.lambda_handler"
  source_code_hash = data.archive_file.postcode_etl_sns_function.output_base64sha256
  runtime          = "python3.8"
  publish          = false
  tags             = local.standard_tags
  environment {
    variables = {
      snsARN        = aws_sns_topic.postcode_etl_sns_topic.arn
      LOGGING_LEVEL = local.postcode_etl_sns_logging_level
    }
  }
  vpc_config {
    subnet_ids = [
      data.terraform_remote_state.vpc.outputs.private_subnets[0],
      data.terraform_remote_state.vpc.outputs.private_subnets[1],
      data.terraform_remote_state.vpc.outputs.private_subnets[2]
    ]
    security_group_ids = [
      local.postcode_extract_vpc_security_group
    ]
  }
}

resource "aws_lambda_permission" "allow_cloudwatch_extract" {
  statement_id  = "AllowExecutionFromCloudWatchExtract"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.postcode_etl_sns_lambda.function_name
  principal     = "logs.${var.aws_region}.amazonaws.com"
  source_arn    = data.aws_cloudwatch_log_group.postcode_etl_extract_log_group.arn
}

resource "aws_cloudwatch_log_subscription_filter" "postcode_etl_extract_sns_cloudwatch_log_trigger" {
  depends_on      = [aws_lambda_permission.allow_cloudwatch_extract]
  name            = local.postcode_etl_sns_cloudwatch_event_name
  log_group_name  = data.aws_cloudwatch_log_group.postcode_etl_extract_log_group.name
  filter_pattern  = "?ERROR ?WARN ?5xx"
  destination_arn = aws_lambda_function.postcode_etl_sns_lambda.arn
}

resource "aws_lambda_permission" "allow_cloudwatch_insert" {
  statement_id  = "AllowExecutionFromCloudWatchInsert"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.postcode_etl_sns_lambda.function_name
  principal     = "logs.${var.aws_region}.amazonaws.com"
  source_arn    = data.aws_cloudwatch_log_group.postcode_etl_insert_log_group.arn
}

resource "aws_cloudwatch_log_subscription_filter" "postcode_etl_insert_sns_cloudwatch_log_trigger" {
  depends_on      = [aws_lambda_permission.allow_cloudwatch_insert]
  name            = local.postcode_etl_sns_cloudwatch_event_name
  log_group_name  = data.aws_cloudwatch_log_group.postcode_etl_insert_log_group.name
  filter_pattern  = "?ERROR ?WARN ?5xx"
  destination_arn = aws_lambda_function.postcode_etl_sns_lambda.arn
}