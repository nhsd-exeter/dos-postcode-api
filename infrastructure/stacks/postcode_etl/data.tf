# Lambda
data "archive_file" "postcode_insert_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/uec-sf-postcode-insert"
  output_path = "${path.module}/functions_zip/${local.postcode_insert_function_name}.zip"
}

data "archive_file" "postcode_extract_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions/uec-sf-postcode-extract"
  output_path = "${path.module}/functions_zip/${local.postcode_extract_function_name}.zip"
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

data "aws_security_group" "sf_read_replica_db_sg" {
  name = var.sf_read_replica_db_sg
}

# data "aws_lambda_layer_version" "dos_python_libs" {
#   layer_name = var.core_dos_python_libs
# }

data "aws_secretsmanager_secret" "dos_read_replica_secret_name" {
  name = var.dos_read_replica_secret_name
}
