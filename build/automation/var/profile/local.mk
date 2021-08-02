-include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables

PROFILE := local

CERTIFICATE_DOMAIN := certificate/certificate
SERVER_PORT := 8443
VERSION := v0.0.1

POSTCODE_LOCATION_DYNAMO_URL := http://host.docker.internal:8000/
DYNAMODB_POSTCODE_LOC_MAP_TABLE := service-finder-nonprod-postcode-location-mapping

ALLOWED_ORIGINS := *
