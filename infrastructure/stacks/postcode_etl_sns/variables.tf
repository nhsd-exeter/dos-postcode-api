# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "vpc_terraform_state_key" { description = "The VPC key in the terraform state bucket" }

# === Stack Specific ============================================================

variable "service_prefix" { description = "service prefix for all infrastructure related to this application" }

variable "postcode_etl_sns_email" { description = "email desitination for critical failures of etl process" }

variable "postcode_etl_sns_logging_level" { description = "Logging level for postcode_etl_sns lambda" }

variable "postcode_etl_extract_alarm_period" { description = "The period in seconds which the alarm monitors if the extract lambda has triggered" }

# variable "postcode_etl_insert_alarm_period" { description = "The period in seconds which the alarm monitors if the insert lambda has triggered" }

variable "texas_vpc_name" { description = "VPC Name for service finder provided by texas" }
