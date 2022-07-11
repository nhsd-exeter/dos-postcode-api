output "postcode_insert_lambda_arn" {
  value = aws_lambda_function.postcode_insert_lambda.arn
}

output "postcode_extract_lambda_arn" {
  value = aws_lambda_function.postcode_extract_lambda.arn
}

output "region_update_lambda_arn" {
  value = aws_lambda_function.region_update_lambda.arn
}
