locals {

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder"
    "Product"     = "service-finder"
    "Environment" = var.profile
    "backup_plan" = "local-4-per-day-keep-14-days"
  }

}
