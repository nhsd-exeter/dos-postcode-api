
resource "aws_lambda_function" "region_update_lambda" {
  filename         = data.archive_file.region_update_function.output_path
  function_name    = local.region_update_function_name
  description      = local.region_update_description
  role             = aws_iam_role.region_update_lambda_role.arn
  handler          = "region_update.lambda_handler"
  source_code_hash = data.archive_file.region_update_function.output_base64sha256
  runtime          = local.region_update_runtime
  timeout          = local.region_update_timeout
  memory_size      = local.region_update_memory_size
  publish          = false
  tags             = local.standard_tags
  environment {
    variables = {
      SOURCE_BUCKET              = local.postcode_etl_s3_bucket
      DYNAMODB_DESTINATION_TABLE = local.region_update_dynamoDb_destination_table
      LOGGING_LEVEL              = var.postcode_etl_logging_level
      CCG_CSV_KEY                = "master_ccg_file.csv"

    }
  }
  vpc_config {
    subnet_ids = [
      data.terraform_remote_state.vpc.outputs.private_subnets[0],
      data.terraform_remote_state.vpc.outputs.private_subnets[1],
      data.terraform_remote_state.vpc.outputs.private_subnets[2]
    ]
    security_group_ids = [aws_security_group.region_lambda_sg.id]
  }
}

resource "aws_security_group" "region_lambda_sg" {
  name        = "${var.service_prefix}-region-lambda-sg"
  description = "Security group for the region update lambda"
  vpc_id      = data.terraform_remote_state.vpc.outputs.vpc_id

  tags = local.standard_tags
}

resource "aws_security_group_rule" "region_lambda_egress_443" {
  type              = "egress"
  from_port         = "443"
  to_port           = "443"
  protocol          = "tcp"
  security_group_id = aws_security_group.region_lambda_sg.id
  cidr_blocks       = ["0.0.0.0/0"]
  description       = "A rule to allow outgoing connections AWS APIs from the lambda Security Group"
}

resource "aws_iam_role" "region_update_lambda_role" {
  name               = local.region_update_iam_name
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

resource "aws_iam_role_policy" "uec-sf-region-update" {
  name   = local.region_update_policy_name
  role   = aws_iam_role.region_update_lambda_role.name
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
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

resource "aws_iam_role_policy_attachment" "AWSLambdaVPCAccessExecutionRoleUpdate" {
  role       = aws_iam_role.region_update_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "s3FullAccessUpdate" {
  role       = aws_iam_role.region_update_lambda_role.name
  policy_arn = local.s3_full_access_policy_arn
}

resource "aws_iam_role_policy_attachment" "dynamoDbFullAccessUpdate" {
  role       = aws_iam_role.region_update_lambda_role.name
  policy_arn = local.dynamoDb_full_access_policy_arn
}

resource "aws_iam_role_policy_attachment" "lambdaFullAcessUpdate" {
  role       = aws_iam_role.region_update_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSLambda_FullAccess"

}

resource "aws_cloudwatch_event_rule" "region_update_cloudwatch_event" {
  name                = local.region_update_cloudwatch_event_name
  description         = local.region_update_cloudwatch_event_description
  schedule_expression = local.region_update_cloudwatch_event_cron_expression
}

resource "aws_cloudwatch_event_target" "daily_region_update_job" {
  rule      = aws_cloudwatch_event_rule.region_update_cloudwatch_event.name
  target_id = local.region_update_cloudwatch_event_target
  arn       = aws_lambda_function.region_update_lambda.arn
}

resource "aws_lambda_permission" "allow_cloudwatch_to_call_region_postcode" {
  statement_id  = local.region_update_cloudwatch_event_statement
  action        = local.region_update_cloudwatch_event_action
  function_name = aws_lambda_function.region_update_lambda.function_name
  principal     = local.region_update_cloudwatch_event_princinple
  source_arn    = aws_cloudwatch_event_rule.region_update_cloudwatch_event.arn
}

resource "aws_cloudwatch_log_group" "region_update_log_group" {
  name = "/aws/lambda/${aws_lambda_function.region_update_lambda.function_name}"
}

resource "aws_lambda_provisioned_concurrency_config" "region_update_concurrency" {
  function_name                     = aws_lambda_function.region_update_lambda.function_name
  provisioned_concurrent_executions = 10
  qualifier                         = aws_lambda_function.region_update_lambda.version
}
