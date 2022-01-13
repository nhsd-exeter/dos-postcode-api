resource "aws_s3_bucket" "postcode_etl_s3" {
  bucket = var.sf_resources_bucket
  acl    = "private"
  force_destroy           = false

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