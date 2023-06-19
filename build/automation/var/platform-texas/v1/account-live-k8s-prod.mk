AWS_ACCOUNT_ID = $(AWS_ACCOUNT_ID_PROD)
AWS_ACCOUNT_NAME = prod

TEXAS_CERTIFICATE_ID = 8b67daa2-2b82-4287-b925-d74ab9fa68ce
# TEXAS_WAF_ACL_ID = ff530a4e-689d-4d90-b3ab-ac2160b5863d

TF_VAR_terraform_platform_state_store = nhsd-texasplatform-terraform-state-store-lk8s-$(AWS_ACCOUNT_NAME)

# ==============================================================================

include $(VAR_DIR)/platform-texas/platform-texas-v1.mk
