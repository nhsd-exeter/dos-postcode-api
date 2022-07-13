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


resource "aws_s3_bucket_object" "eccg_file" {

  bucket = var.sf_resources_bucket

  key = "eccg.csv"

  acl = "private"

  source = "${path.module}/functions/eccg.csv"

  etag = filemd5("${path.module}/functions/eccg.csv")

  force_destroy = true

}

resource "aws_s3_bucket_object" "pcodey56" {

  bucket = var.sf_resources_bucket

  key = "pcodey56.csv"

  acl = "private"

  source = "${path.module}/functions/pcodey56.csv"

  etag = filemd5("${path.module}/functions/pcodey56.csv")

  force_destroy = true

}

resource "aws_s3_bucket_object" "pcodey58" {

  bucket = var.sf_resources_bucket

  key = "pcodey58.csv"

  acl = "private"

  source = "${path.module}/functions/pcodey58.csv"

  etag = filemd5("${path.module}/functions/pcodey58.csv")

  force_destroy = true

}

resource "aws_s3_bucket_object" "pcodey59" {

  bucket = var.sf_resources_bucket

  key = "pcodey59.csv"

  acl = "private"

  source = "${path.module}/functions/pcodey59.csv"

  etag = filemd5("${path.module}/functions/pcodey59.csv")

  force_destroy = true

}

resource "aws_s3_bucket_object" "pcodey60" {

  bucket = var.sf_resources_bucket

  key = "pcodey60.csv"

  acl = "private"

  source = "${path.module}/functions/pcodey60.csv"

  etag = filemd5("${path.module}/functions/pcodey60.csv")

  force_destroy = true

}

resource "aws_s3_bucket_object" "pcodey61" {

  bucket = var.sf_resources_bucket

  key = "pcodey61.csv"

  acl = "private"

  source = "${path.module}/functions/pcodey61.csv"

  etag = filemd5("${path.module}/functions/pcodey61.csv")

  force_destroy = true

}

resource "aws_s3_bucket_object" "pcodey62" {

  bucket = var.sf_resources_bucket

  key = "pcodey62.csv"

  acl = "private"

  source = "${path.module}/functions/pcodey62.csv"

  etag = filemd5("${path.module}/functions/pcodey62.csv")

  force_destroy = true

}

resource "aws_s3_bucket_object" "pcodey63" {

  bucket = var.sf_resources_bucket

  key = "pcodey63.csv"

  acl = "private"

  source = "${path.module}/functions/pcodey63.csv"

  etag = filemd5("${path.module}/functions/pcodey63.csv")

  force_destroy = true

}
