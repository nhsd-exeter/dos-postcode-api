# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "vpc_terraform_state_key" { description = "The VPC key in the terraform state bucket" }

# === Stack Specific ============================================================

variable "postcode_mapping_dynamo_name" { description = "The name of the postcode mapping dynamo database." }

variable "sf_resources_bucket" { description = "Bucket containing resources and images that the application pulls from in real time." }

variable "sf_read_replica_db" { description = "The SF read replica to get the postcodes from" }

variable "sf_read_replica_db_sg" { description = "The security group for the dos SF read replica" }

variable "dos_read_replica_secret_name" { description = "The dos read replica secret name" }

variable "dos_read_replica_key" { description = "The dos read replica secret key" }

variable "postcode_etl_db_user" { description = "The dos read replica user name" }

variable "postcode_etl_source_db" { description = "The dos read replica source database" }

variable "core_dos_python_libs" { description = "core dos python libs for accessing dos databases" }

variable "service_prefix" { description = "service prefix for all infrastructure related to this application" }

variable "postcode_etl_logging_level" { description = "Logging level for service_etl lambda" }
