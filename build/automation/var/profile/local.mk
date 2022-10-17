-include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables

PROFILE := local
ENVIRONMENT := $(PROFILE)
SPRING_PROFILES_ACTIVE := $(PROFILE),mock-auth

SERVER_PORT := 443
VERSION := v0.0.1

POSTCODE_LOCATION_DYNAMO_URL := http://dynamo.pc.local:8000/
DYNAMODB_POSTCODE_LOC_MAP_TABLE := service-finder-${PROFILE}-postcode-location-mapping

CERTIFICATE_DOMAIN := certificate
