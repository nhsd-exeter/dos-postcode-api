output "ccg_insert_lambda_arn" {
  value = aws_lambda_function.ccg_insert_lambda.arn
}

output "ccg_extract_lambda_arn" {
  value = aws_lambda_function.ccg_extract_lambda.arn
}
