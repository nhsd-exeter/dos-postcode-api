locals {

  # postcode_insert_function_name  = "${var.service_prefix}-postcode-insert"
  postcode_extract_function_name = "${var.service_prefix}-postcode-extract"

  postcode_etl_sns_name                         = "${var.service_prefix}-postcode-etl-sns"
  postcode_etl_sns_description                  = "Triggers when the service-etl process fails and sends a notification to the sns service"
  postcode_etl_sns_runtime                      = "python3.7"
  postcode_etl_sns_policy_name                  = "${var.service_prefix}-postcode-etl_sns_policy"
  postcode_etl_sns_cloudwatch_event_name        = "${var.service_prefix}-postcode-etl-sns-rule"
  postcode_etl_sns_cloudwatch_event_description = "Log event watcher to send an SNS event whenever an error log event is recorded"
  postcode_etl_sns_logging_level                = var.postcode_etl_sns_logging_level

  postcode_etl_extract_alarm_name = "${var.service_prefix}-postcode-extract-alarm"

  # postcode_etl_insert_alarm_name = "${var.service_prefix}-postcode-insert-alarm"

  standard_tags = {
    "Programme"        = "uec"
    "Service"          = "service-finder"
    "Product"          = "service-finder"
    "service_prefixes" = "uec-sf"
    "tag"              = "uec-sf"
    "uc_name"          = "UECSF"
    "Environment"      = var.profile
  }

}
