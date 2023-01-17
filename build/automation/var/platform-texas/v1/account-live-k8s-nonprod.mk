AWS_ACCOUNT_ID = $(AWS_ACCOUNT_ID_NONPROD)
AWS_ACCOUNT_NAME = nonprod

TEXAS_CERTIFICATE_ID = c0718115-4e22-4f48-a4aa-8c16ea86c5e6
TEXAS_WAF_ACL_ID = dfae6ec3-aa05-428f-a022-5fd85f646009

TF_VAR_terraform_platform_state_store = nhsd-texasplatform-terraform-state-store-live-lk8s-$(AWS_ACCOUNT_NAME)
TF_VAR_project_id := $(PROJECT_ID)
TF_VAR_eks_terraform_state_key = eks/terraform.tfstate
TF_VAR_aws_account_id = $(AWS_ACCOUNT_ID_NONPROD)

JENKINS_ENV = live-lk8s-nonprod
JENKINS_SERVICE_TEAM = uec-sf


# ==============================================================================

include $(VAR_DIR)/platform-texas/platform-texas-v1.mk
