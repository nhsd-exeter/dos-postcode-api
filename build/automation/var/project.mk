ORG_NAME = nhsd-exeter
PROGRAMME = uec
PROJECT_GROUP = uec/dos-api
PROJECT_GROUP_SHORT = uec-dos-api
PROJECT_NAME = dos-postcode-api
PROJECT_NAME_SHORT = pc
PROJECT_DISPLAY_NAME = DoS Postcode API
PROJECT_ID := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)
TEAM_NAME = Service Finder
TEAM_ID = service-finder

ROLE_PREFIX = UECDoSAPI
PROJECT_TAG = $(PROJECT_NAME)
SERVICE_TAG = $(PROJECT_GROUP_SHORT)
SERVICE_TAG_COMMON = texas
SERVICE_DEVELOPER_ROLE = texas:uecdosapi-service-developer

PROJECT_TECH_STACK_LIST = java,terraform

DOCKER_REPOSITORIES =
SSL_DOMAINS_PROD =
DEPLOYMENT_SECRETS = $(PROJECT_ID)-$(PROFILE)/deployment

AUTHENTICATION_ENDPOINT = https://uec-dos-api-sfsa-$(PROFILE)-uec-dos-api-sfs-service.$(TEXAS_HOSTED_ZONE)/authentication/login
POSTCODE_USER = fuzzy-search-api@nhs.net
POSTCODE_DOMAIN = $(PROJECT_ID)-$(PROFILE)-uec-dos-api-pc-ingress.$(TEXAS_HOSTED_ZONE)
POSTCODE_ENDPOINT = https://$(POSTCODE_DOMAIN)

APPLICATION_SA_NAME := $(PROJECT_ID)-service-account
TF_VAR_application_service_account_name := $(APPLICATION_SA_NAME)
TF_VAR_project_namespace := $(Project_NameSpace)
