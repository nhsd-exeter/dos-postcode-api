TEXAS_VERSION = v2

AWS_DEFAULT_REGION = eu-west-2
AWS_ALTERNATIVE_REGION = eu-west-1
AWS_REGION = $(AWS_DEFAULT_REGION)
AWS_PROFILE = $(or $(TEXAS_PROFILE), $(PROJECT_ID)-$(AWS_ACCOUNT_NAME))

AWS_ROLE_ADMIN = NHSDServiceTeamAdminRole
AWS_ROLE_READONLY = NHSDServiceTeamReadOnlyRole
AWS_ROLE_DEVELOPER = NHSDServiceTeamDeveloperRole
AWS_ROLE_SUPPORT = NHSDServiceTeamSupportRole
AWS_ROLE_PIPELINE = NHSDServiceTeamDeploymentRole
