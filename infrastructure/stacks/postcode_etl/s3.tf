resource "aws_s3_bucket" "postcode_etl_s3" {
  bucket        = var.sf_resources_bucket
  acl           = "private"
  force_destroy = false

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }

  versioning {
    enabled = true
  }

  tags = local.standard_tags
}

resource "aws_lambda_permission" "s3_trigger_permission" {
  statement_id  = "AllowExecutionFromS3"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.postcode_insert_lambda.function_name
  principal     = "s3.amazonaws.com"

  source_arn = aws_s3_bucket.postcode_etl_s3.arn
}

resource "aws_s3_bucket_notification" "postcode_etl_s3_bucket_notification" {
  bucket = aws_s3_bucket.postcode_etl_s3.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.postcode_insert_lambda.arn
    events              = ["s3:ObjectCreated:*"]
    filter_prefix       = "postcode_locations/"
  }
}

resource "aws_s3_bucket_public_access_block" "postcode_etl_s3_block_public_access" {
  bucket                  = aws_s3_bucket.postcode_etl_s3.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
