-include $(VAR_DIR)/platform-texas/v1/account-live-k8s-prod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/8d65eee4-cf92-4a00-84de-7a9f544ba724
ECR_URL := $(AWS_ECR)/$(PROJECT_GROUP_SHORT)/$(PROJECT_NAME_SHORT)/$(PROJECT_NAME)

PROFILE := demo
ENVIRONMENT := $(PROFILE)
SPRING_PROFILES_ACTIVE := $(PROFILE)

DEPLOYMENT_STACKS := application
INFRASTRUCTURE_STACKS := datastore,postcode_etl
SNS_INFRASTRUCTURE_STACKS := postcode_etl_sns

SERVER_PORT := 443

POSTCODE_LOCATION_DYNAMO_URL := https://dynamodb.$(AWS_REGION).amazonaws.com
DYNAMODB_POSTCODE_LOC_MAP_TABLE := $(PROJECT_GROUP_NAME_SHORT)-$(PROFILE)-postcode-location-mapping

REPLICAS := 3
PROJECT_GROUP_NAME_SHORT := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)

TF_VAR_service_prefix := $(PROJECT_GROUP_NAME_SHORT)-$(PROFILE)
TF_VAR_postcode_mapping_dynamo_name := $(TF_VAR_service_prefix)-postcode-location-mapping
TF_VAR_sf_resources_bucket := $(TF_VAR_service_prefix)-application-resources
TF_VAR_postcode_etl_logging_level := INFO
TF_VAR_postcode_etl_sns_logging_level := INFO
TF_VAR_postcode_etl_sns_email := postcode-etl-alerts-d-aaaafmi4x7xvayo2zzqr5sojpa@a2si.slack.com

TF_VAR_sf_read_replica_db := uec-core-dos-put-db-replica-sf.dos-db-put
TF_VAR_sf_read_replica_db_sg := uec-core-dos-put-db-12-replica-sf-sg
TF_VAR_dos_read_replica_secret_name := core-dos-uet-database-upgrade/deployment
TF_VAR_dos_read_replica_key := DB_SF_READONLY_PASSWORD
TF_VAR_postcode_etl_db_user := dos_sf_readonly
TF_VAR_postcode_etl_source_db := pathwaysdos_ut
TF_VAR_core_dos_python_libs := core-dos-python-libs

CERTIFICATE_DOMAIN := certificate
CHECK_DEPLOYMENT_TIME_LIMIT := 600
CHECK_DEPLOYMENT_POLL_INTERVAL := 10
