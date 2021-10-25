# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

# === Stack Specific ============================================================

variable "postcode_mapping_dynamo_name" { description = "The name of the postcode mapping dynamo database." }
