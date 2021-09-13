-include $(VAR_DIR)/platform-texas/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/c0718115-4e22-4f48-a4aa-8c16ea86c5e6

PROFILE := dev

CERTIFICATE_DOMAIN := certificate/certificate
SERVER_PORT := 8443
VERSION := v0.0.1

POSTCODE_LOCATION_DYNAMO_URL := https://dynamodb.$(AWS_REGION).amazonaws.com
DYNAMODB_POSTCODE_LOC_MAP_TABLE := service-finder-nonprod-postcode-location-mapping

ALLOWED_ORIGINS := *
