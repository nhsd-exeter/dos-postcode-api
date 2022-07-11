
resource "aws_lambda_function" "file_generator_lambda" {
  filename         = data.archive_file.file_generator_function.output_path
  function_name    = local.file_generator_function_name
  description      = local.file_generator_description
  role             = aws_iam_role.file_generator_lambda_role.arn
  handler          = "file_generator.lambda_handler"
  source_code_hash = data.archive_file.file_generator_function.output_base64sha256
  runtime          = local.file_generator_runtime
  timeout          = local.file_generator_timeout
  memory_size      = local.file_generator_memory_size
  publish          = false
  tags             = local.standard_tags
  environment {
    variables = {
      SOURCE_BUCKET = local.postcode_etl_s3_bucket
      LOGGING_LEVEL = var.postcode_etl_logging_level
    }
  }
  vpc_config {
    subnet_ids = [
      data.terraform_remote_state.vpc.outputs.private_subnets[0],
      data.terraform_remote_state.vpc.outputs.private_subnets[1],
      data.terraform_remote_state.vpc.outputs.private_subnets[2]
    ]
    security_group_ids = [aws_security_group.file_generator_lambda_sg.id]
  }
}

resource "aws_security_group" "file_generator_lambda_sg" {
  name        = "${var.service_prefix}-ccg-file-generator-lambda-sg"
  description = "Security group for the ccg-file-generator lambda"
  vpc_id      = data.terraform_remote_state.vpc.outputs.vpc_id

  tags = local.standard_tags
}

resource "aws_security_group_rule" "file_generator_lambda_egress_443" {
  type              = "egress"
  from_port         = "443"
  to_port           = "443"
  protocol          = "tcp"
  security_group_id = aws_security_group.file_generator_lambda_sg.id
  cidr_blocks       = ["0.0.0.0/0"]
  description       = "A rule to allow outgoing connections AWS APIs from the lambda Security Group"
}

resource "aws_iam_role" "file_generator_lambda_role" {
  name               = local.file_generator_iam_name
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

resource "aws_iam_role_policy" "uec-sf-file-generator" {
  name   = local.file_generator_policy_name
  role   = aws_iam_role.file_generator_lambda_role.name
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

resource "aws_iam_role_policy_attachment" "AWSLambdaVPCAccessExecutionRoleUpdate_generator" {
  role       = aws_iam_role.file_generator_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "s3FullAccessUpdate_generator" {
  role       = aws_iam_role.file_generator_lambda_role.name
  policy_arn = local.s3_full_access_policy_arn
}

resource "aws_iam_role_policy_attachment" "dynamoDbFullAccessUpdate_generator" {
  role       = aws_iam_role.file_generator_lambda_role.name
  policy_arn = local.dynamoDb_full_access_policy_arn
}

resource "aws_iam_role_policy_attachment" "lambdaFullAcessUpdate_generator" {
  role       = aws_iam_role.file_generator_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSLambda_FullAccess"

}

# resource "aws_cloudwatch_event_rule" "region_update_cloudwatch_event" {
#   name                = local.region_update_cloudwatch_event_name
#   description         = local.region_update_cloudwatch_event_description
#   schedule_expression = local.region_update_cloudwatch_event_cron_expression
# }

# resource "aws_cloudwatch_event_target" "daily_region_update_job" {
#   rule      = aws_cloudwatch_event_rule.region_update_cloudwatch_event.name
#   target_id = local.region_update_cloudwatch_event_target
#   arn       = aws_lambda_function.region_update_lambda.arn
# }

# resource "aws_lambda_permission" "allow_cloudwatch_to_call_region_postcode" {
#   statement_id  = local.region_update_cloudwatch_event_statement
#   action        = local.region_update_cloudwatch_event_action
#   function_name = aws_lambda_function.region_update_lambda.function_name
#   principal     = local.region_update_cloudwatch_event_princinple
#   source_arn    = aws_cloudwatch_event_rule.region_update_cloudwatch_event.arn
# }

resource "aws_cloudwatch_log_group" "file_generator_log_group" {
  name = "/aws/lambda/${aws_lambda_function.file_generator_lambda.function_name}"
}
