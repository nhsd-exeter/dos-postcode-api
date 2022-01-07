# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "aws_account_id" { description = "Texas AWS account id" }

variable "platform_zone" { description = "The hosted zone used for the platform" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

variable "sf_resources_bucket" { description = "Bucket containing resources and images that the application pulls from in real time." }

# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "service_prefix" { description = "The prefix to be used for all infrastructure" }
