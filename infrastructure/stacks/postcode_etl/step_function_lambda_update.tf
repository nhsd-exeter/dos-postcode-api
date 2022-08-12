resource "aws_sfn_state_machine" "sfn_state-machine_region_update" {
  name       = "${var.service_prefix}-region-email-update-state-machine"
  role_arn   = "${aws_iam_role.iam_role_for_sfn.arn}"
  definition = <<EOF

  {
    "StartAt":"region-update-lambda-step",
    "States": {
        "region-update-lambda-step" :{
          "Comment": "Trigger region update parallel exceutions",
          "Type": "Parallel",
          "End": true,
          "Branches": [
            {
              "StartAt":"region_update",
              "States": {
                "region_update" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update"
                },
                "email_update" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            },
            {
              "StartAt":"region_update_2",
              "States": {
                "region_update_2" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update_2"
                },
                "email_update_2" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            },
            {
              "StartAt":"region_update_3",
              "States": {
                "region_update_3" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update_3"
                },
                "email_update_3" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            },
            {
              "StartAt":"region_update_4",
              "States": {
                "region_update_4" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update_4"
                },
                "email_update_4" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            },
            {
              "StartAt":"region_update_5",
              "States": {
                "region_update_5" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update_5"
                },
                "email_update_5" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            },
            {
              "StartAt":"region_update_6",
              "States": {
                "region_update_6" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update_6"
                },
                "email_update_6" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            },
            {
              "StartAt":"region_update_7",
              "States": {
                "region_update_7" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update_7"
                },
                "email_update_7" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            },
            {
              "StartAt":"region_update_8",
              "States": {
                "region_update_8" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update_8"
                },
                "email_update_8" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            },
            {
              "StartAt":"region_update_9",
              "States": {
                "region_update_9" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.region_update_lambda.arn}",
                  "Next": "email_update_9"
                },
                "email_update_9" :{
                  "Type": "Task",
                  "Resource": "${aws_lambda_function.email_update_lambda.arn}",
                  "End": true
                }
              }
            }
          ]
        }
    }
  }
  EOF

  depends_on = [
    aws_lambda_function.region_update_lambda, aws_lambda_function.email_update_lambda
  ]
}

# Create IAM role for AWS Step Function
resource "aws_iam_role" "iam_role_for_sfn" {
  name = "${var.service_prefix}-step-function-iam-role"

  assume_role_policy = data.aws_iam_policy_document.iam_for_sfn_document.json
}

data "aws_iam_policy_document" "iam_for_sfn_document" {
  statement {
    actions = [
      "sts:AssumeRole"
    ]

    principals {
      type = "Service"
      identifiers = [
        "states.amazonaws.com",
        "events.amazonaws.com"
      ]
    }
  }
}

resource "aws_iam_policy" "policy_invoke_lambda" {
  name = "${var.service_prefix}-policy-step-invoke-lambda"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "lambda:InvokeFunction",
                "lambda:InvokeAsync"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}


// Attach policy to IAM Role for Step Function
resource "aws_iam_role_policy_attachment" "iam_for_sfn_attach_policy_invoke_lambda" {
  role       = "${aws_iam_role.iam_role_for_sfn.name}"
  policy_arn = "${aws_iam_policy.policy_invoke_lambda.arn}"
}


//Cloud watch trigger to run daily
resource "aws_cloudwatch_event_rule" "step_function_cloudwatch_event" {
  name                = local.step_function_cloudwatch_event_name
  description         = local.step_function_cloudwatch_event_description
  schedule_expression = local.step_function_cloudwatch_event_cron_expression
}

resource "aws_cloudwatch_event_target" "daily_step_function_job" {
  rule      = aws_cloudwatch_event_rule.step_function_cloudwatch_event.name
  target_id = local.step_function_cloudwatch_event_target
  arn       = aws_sfn_state_machine.sfn_state-machine_region_update.arn
  role_arn  = "${aws_iam_role.iam_role_for_sfn.arn}"
}


resource "aws_cloudwatch_log_group" "step_function_log_group" {
  name = "/aws/states/${aws_sfn_state_machine.sfn_state-machine_region_update.name}"
}
