provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
  version = ">= 3.74.1, < 4.0.0"
}

provider "archive" {
  version = "2.2.0"
}
