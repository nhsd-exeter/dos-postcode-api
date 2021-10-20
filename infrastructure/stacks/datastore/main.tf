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
  tags = local.standard_tags
}
