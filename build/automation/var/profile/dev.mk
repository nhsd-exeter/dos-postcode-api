-include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/c0718115-4e22-4f48-a4aa-8c16ea86c5e6
ECR_URL := $(AWS_ECR)/$(PROJECT_GROUP_SHORT)/$(PROJECT_NAME_SHORT)/$(PROJECT_NAME)

PROFILE := dev
ENVIRONMENT := $(PROFILE)
SPRING_PROFILES_ACTIVE := $(PROFILE)

DEPLOYMENT_STACKS := application

SERVER_PORT := 443
IMAGE_TAG := v0.0.1

POSTCODE_LOCATION_DYNAMO_URL := https://dynamodb.$(AWS_REGION).amazonaws.com
DYNAMODB_POSTCODE_LOC_MAP_TABLE := service-finder-nonprod-postcode-location-mapping

REPLICAS := 1
PROJECT_GROUP_NAME_SHORT := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)
TTL := 2d
