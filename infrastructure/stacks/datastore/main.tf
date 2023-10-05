# DynamoDB postcode locations
module "dynamodb_table_postcode" {
  source  = "terraform-aws-modules/dynamodb-table/aws"
  version = "v0.11.0"

  name     = var.postcode_mapping_dynamo_name
  hash_key = "postcode"

  attributes = [
    {
      name = "postcode"
      type = "S"
    }
  ]

  billing_mode = "PAY_PER_REQUEST"

  tags = local.standard_tags
}

# resource "aws_dynamodb_table" "dynamodb_table_postcode" {
#   name         = var.postcode_mapping_dynamo_name
#   hash_key     = "postcode"
#   billing_mode = "PAY_PER_REQUEST"

#   attribute {
#     name = "postcode"
#     type = "S"
#   }
#   tags = local.standard_tags

# }
