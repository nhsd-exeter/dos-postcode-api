AWS_ACCOUNT_ID = $(AWS_ACCOUNT_ID_PROD)
AWS_ACCOUNT_NAME = prod

TEXAS_CERTIFICATE_ID = 8b67daa2-2b82-4287-b925-d74ab9fa68ce


TF_VAR_terraform_platform_state_store = nhsd-texasplatform-terraform-state-store-lk8s-$(AWS_ACCOUNT_NAME)

JENKINS_ENV = live-lk8s-prod
JENKINS_SERVICE_TEAM = uec-sf

# ==============================================================================

include $(VAR_DIR)/platform-texas/platform-texas-v1.mk
