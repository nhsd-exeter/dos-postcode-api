# User Management RDS
module "s3-service-bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "1.16.0"

  bucket                  = var.sf_resources_bucket
  force_destroy           = false
  acl                     = "private"
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
  server_side_encryption_configuration = {
    rule = {
      apply_server_side_encryption_by_default = {
        sse_algorithm = "AES256"
      }
    }
  }
  versioning = {
    enabled = true
  }
  tags = local.standard_tags
}
