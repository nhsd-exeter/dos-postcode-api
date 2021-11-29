locals {

  postcode_insert_function_name       = "${var.service_prefix}-postcode-insert"
  postcode_extract_function_name      = "${var.service_prefix}-postcode-extract"
  postcode_extract_vpc_security_group = data.aws_security_group.dos_application_security_group.id

  postcode_etl_sns_name                         = "${var.service_prefix}-postcode-etl-sns"
  postcode_etl_sns_description                  = "Triggers when the service-etl process fails and sends a notification to the sns service"
  postcode_etl_sns_runtime                      = "python3.7"
  postcode_etl_sns_policy_name                  = "${var.service_prefix}-postcode-etl_sns_policy"
  postcode_etl_sns_cloudwatch_event_name        = "${var.service_prefix}-postcode-etl-sns-rule"
  postcode_etl_sns_cloudwatch_event_description = "Log event watcher to send an SNS event whenever an error log event is recorded"
  postcode_etl_sns_logging_level                = var.postcode_etl_sns_logging_level

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "dos-api"
    "Product"     = "postcode"
    "Environment" = var.profile
  }

}