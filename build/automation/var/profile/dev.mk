-include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/$(TEXAS_CERTIFICATE_ID)
ECR_URL := $(AWS_ECR)/$(PROJECT_GROUP_SHORT)/$(PROJECT_NAME_SHORT)/$(PROJECT_NAME)
ECR_TEXAS_URL_NONPROD = $(AWS_ECR_NON_PROD)/texas

PROFILE := dev
ENVIRONMENT := $(PROFILE)
SPRING_PROFILES_ACTIVE := $(PROFILE),mock-auth

SLEEP_AFTER_PLAN := 30s

DEPLOYMENT_STACKS := application
SNS_INFRASTRUCTURE_STACKS := postcode_etl_sns
INFRASTRUCTURE_STACKS_ETL := postcode_etl
INFRASTRUCTURE_STACK_STORE := datastore
INFRASTRUCTURE_STACK_FIREWALL := firewall
INFRASTRUCTURE_STACKS := $(INFRASTRUCTURE_STACK_STORE),$(INFRASTRUCTURE_STACKS_ETL),$(INFRASTRUCTURE_STACK_FIREWALL),$(SNS_INFRASTRUCTURE_STACKS)


SERVER_PORT := 443
IMAGE_TAG := v0.0.1
VERSION := v0.0.1
JMETER_MASTER_IMAGE := jmeter-master:5.4.1-log4j2-patch
JMETER_SLAVE_IMAGE := jmeter-slave:5.4.1-log4j2-patch

POSTCODE_LOCATION_DYNAMO_URL := https://dynamodb.$(AWS_REGION).amazonaws.com
DYNAMODB_POSTCODE_LOC_MAP_TABLE := $(PROJECT_GROUP_NAME_SHORT)-$(PROFILE)-postcode-location-mapping

REPLICAS := 1
PROJECT_GROUP_NAME_SHORT := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)
TTL := 2d

SERVICE_PREFIX := $(PROJECT_GROUP_NAME_SHORT)-$(PROFILE)
TF_VAR_service_prefix := $(PROJECT_GROUP_NAME_SHORT)-$(PROFILE)
TF_VAR_postcode_mapping_dynamo_name := $(TF_VAR_service_prefix)-postcode-location-mapping
TF_VAR_region_mapping_dynamo_name := $(TF_VAR_service_prefix)-region-location-mapping
TF_VAR_sf_resources_bucket := $(TF_VAR_service_prefix)-application-resources
TF_VAR_postcode_etl_logging_level := INFO
TF_VAR_postcode_etl_sns_logging_level := INFO
TF_VAR_postcode_etl_sns_email := postcode-etl-alerts-d-aaaae6gm4hnjepwaspcqoeuh7i@a2si.slack.com
TF_VAR_postcode_etl_extract_alarm_period := 86400
TF_VAR_postcode_etl_insert_alarm_period := 86400

CERTIFICATE_DOMAIN := certificate
CHECK_DEPLOYMENT_TIME_LIMIT := 600
CHECK_DEPLOYMENT_POLL_INTERVAL := 10

# Connection to DoS Read Replica for extraction Lambdas
TF_VAR_sf_read_replica_db := core-dos-regression-sf-replica.dos-db-rds
TF_VAR_sf_read_replica_db_sg := live-lk8s-nonprod-uec-sf-core-dos-sf-replica-sg
TF_VAR_dos_read_replica_secret_name := core-dos-dev/deployment
TF_VAR_dos_read_replica_key := DB_SF_READONLY_PASSWORD
TF_VAR_postcode_etl_db_user := dos_sf_readonly
TF_VAR_postcode_etl_source_db := pathwaysdos_regression
TF_VAR_core_dos_python_libs := core-dos-python-libs

TF_VAR_texas_vpc_name = lk8s-$(AWS_ACCOUNT_NAME).texasplatform.uk

# ===================== firewall
WAF_NAME = $(SERVICE_PREFIX)-waf-acl
TF_VAR_waf_name = $(WAF_NAME)
TF_VAR_waf_dashboard_name = $(SERVICE_PREFIX)-wafv2-dashboard
TF_VAR_waf_log_group_name = aws-waf-logs-$(SERVICE_PREFIX)
TF_VAR_non_gb_rule_metric_name = $(SERVICE_PREFIX)-waf-non-GB-geo-match-metric
TF_VAR_ip_reputation_list_metric_name = $(SERVICE_PREFIX)-waf-aws-ip-reputation-list-metric
TF_VAR_common_rule_set_metric_name = $(SERVICE_PREFIX)-waf-aws-common-rule-set-metric
TF_VAR_sql_injection_rules_metric = $(SERVICE_PREFIX)-waf-aws-bad-inputs-rule-set-metric
TF_VAR_bad_input_metric_name = $(SERVICE_PREFIX)-waf-aws-bad-inputs-rule-set-metric
