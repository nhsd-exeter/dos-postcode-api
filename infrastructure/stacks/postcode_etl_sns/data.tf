# Lambda

data "archive_file" "postcode_etl_sns_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/postcode_etl_sns"
  output_path = "${path.module}/functions_zip/${local.postcode_etl_sns_name}.zip"
}

# Texas-managed VPC info
data "aws_vpcs" "vpcs" {
  tags = {
    Name = var.texas_vpc_name
  }
}

data "aws_vpc" "vpc" {
  count = length(data.aws_vpcs.vpcs.ids)
  id    = tolist(data.aws_vpcs.vpcs.ids)[count.index]
}

data "aws_subnet_ids" "texas_subnet_ids" {
  vpc_id = data.aws_vpc.vpc[0].id
}

data "aws_subnet_ids" "texas_private_subnet_ids_filtered" {
  vpc_id = data.aws_vpc.vpc[0].id
  filter {
    name   = "tag:Name"
    values = ["*private*"]
  }
}

# Texas private subnets as a map of objects (keyed on subnet ID)
data "aws_subnet" "texas_private_subnet_ids_as_map_of_objects" {
  for_each = data.aws_subnet_ids.texas_private_subnet_ids_filtered.ids
  id       = each.value
}

# Texas private subnets as an array of objects (numerically keyed)
data "aws_subnet" "texas_private_subnet_ids_as_array_of_objects" {
  count = length(data.aws_subnet_ids.texas_private_subnet_ids_filtered.ids)
  id    = tolist(data.aws_subnet_ids.texas_private_subnet_ids_filtered.ids)[count.index]
}

data "aws_cloudwatch_log_groups" "extract_log_groups" {
  log_group_name_prefix = "/aws/lambda/${local.postcode_extract_function_name}"
}

# data "aws_cloudwatch_log_groups" "insert_log_groups" {
#   log_group_name_prefix = "/aws/lambda/${local.postcode_insert_function_name}"
# }

# This may not exist at the time of evaluation due to it being created by another terraform root/stack
# So we search for it first in aws.cloudwatch_log_groups.extract_log_groups and only count it if it exists
data "aws_cloudwatch_log_group" "postcode_etl_extract_log_group" {
  count = length(data.aws_cloudwatch_log_groups.extract_log_groups) > 0 ? 1 : 0
  name  = "/aws/lambda/${local.postcode_extract_function_name}"
}

# This may not exist at the time of evaluation due to it being created by another terraform root/stack
# So we search for it first in aws.cloudwatch_log_groups.extract_log_groups and only count it if it exists
# data "aws_cloudwatch_log_group" "postcode_etl_insert_log_group" {
#   count = length(data.aws_cloudwatch_log_groups.insert_log_groups) > 0 ? 1 : 0
#   name  = "/aws/lambda/${local.postcode_insert_function_name}"
# }
