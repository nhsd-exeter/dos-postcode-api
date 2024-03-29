SONAR_SCANNER_CLI_VERSION = 5.0
SONAR_EXCLUSIONS = src/main/java/**/config/*.*,src/main/java/**/domain/*.*,src/main/java/**/exception/*.*,src/test/**/*.*,src/main/java/**/filter/*.*,src/main/java/**/PostcodeMappingApplication.*

sonar-scanner-cli: ### Run Sonar scanner CLI- mandatory: SONAR_HOST_TOKEN; optional: SONAR_HOST_URL=[defaults to 'https://sonarcloud.io'],SONAR_EXCLUSIONS=[e.g. '**/*.java'],SONAR_ARGS
	make docker-run-sonar-scanner-cli CMD=" \
		-D sonar.host.url='$(or $(SONAR_HOST_URL), https://sonarcloud.io)' \
		-D sonar.token='$(SONAR_HOST_TOKEN)' \
		-D sonar.organization='$(ORG_NAME)' \
		-D sonar.projectKey='uec-dos-api-pca' \
		-D sonar.projectName='$(PROJECT_DISPLAY_NAME)' \
		-D sonar.java.version=17
		-D sonar.sourceEncoding='UTF-8' \
		-D sonar.exclusions='$(shell [ $(PROJECT_NAME) != $(DEVOPS_PROJECT_NAME) ] && echo build/automation/**),$(SONAR_EXCLUSIONS)' \
		$(SONAR_ARGS) \
	"
