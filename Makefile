PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)

# ==============================================================================
# Development workflow targets

prepare: ## Prepare environment
	make \
		git-config \
		docker-config

compile:
	make docker-run-mvn \
		DIR="application" \
		CMD="compile"

build: project-config
	cp \
		$(PROJECT_DIR)/build/automation/etc/certificate/* \
		$(PROJECT_DIR)/application/src/main/resources/certificate
	make docker-run-mvn \
		DIR="application" \
		CMD="-Dmaven.test.skip=true clean install"
	mv \
		$(PROJECT_DIR)/application/target/dos-postcode-api-*.jar \
		$(PROJECT_DIR)/build/docker/dos-postcode-api/assets/application/dos-postcode-api.jar
	make docker-build NAME=dos-postcode-api

start: project-start	# Start project
	make local-dynamodb-scripts

local-dynamodb-scripts:
	cd $(PROJECT_DIR)/data/dynamo/test
	chmod +x *.sh
	./00-postcode-location-mapping-table.sh > /dev/null
	./01-postcode-location-mapping-table.sh

stop: project-stop # Stop project

restart: stop start # Restart project

debug:
	make project-start 2> /dev/null ||:
	docker rm --force dos-postcode-api 2> /dev/null ||:
	make docker-run-mvn-lib-mount \
		NAME=dos-postcode-api \
		DIR=application \
		CMD="spring-boot:run \
			-Dspring-boot.run.jvmArguments=' \
				-Xdebug \
				-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:9999 \
			' \
		" \
		ARGS=" \
		--env SPRING_PROFILES_ACTIVE='local' \
		--env DYNAMODB_POSTCODE_LOC_MAP_TABLE='$(DYNAMODB_POSTCODE_LOC_MAP_TABLE)' \
		--env CERTIFICATE_DOMAIN='$(CERTIFICATE_DOMAIN)' \
		--env POSTCODE_LOCATION_DYNAMO_URL='$(POSTCODE_LOCATION_DYNAMO_URL)' \
		--env SERVER_PORT='$(SERVER_PORT)' \
		--env VERSION='$(VERSION)' \
		--env AWS_ACCESS_KEY_ID='dummy' \
		--env AWS_SECRET_ACCESS_KEY='dummy' \
		\
		--publish 9999:9999 \
		--publish 443:443 \
		"
		make project-start

log: project-log # Show project logs

unit-test:
	make docker-run-mvn \
		DIR="application" \
		CMD="test"

coverage-report:
	make unit-test
	make docker-run-mvn \
		DIR="application" \
		CMD="jacoco:report"

test: # Test project
	make start
	make stop

push: # Push project artefacts to the registry
	make docker-push NAME=dos-postcode-api

tag-release: # Create the release tag - mandatory DEV_TAG RELEASE_TAG
	make docker-login
	docker pull $(DOCKER_REGISTRY)/api:$(DEV_TAG)
	docker tag $(DOCKER_REGISTRY)/api:$(DEV_TAG) $(DOCKER_REGISTRY)/api:$(RELEASE_TAG)
	docker tag $(DOCKER_REGISTRY)/api:$(DEV_TAG) $(DOCKER_REGISTRY_LIVE)/api:$(RELEASE_TAG)
	docker push $(DOCKER_REGISTRY)/api:$(RELEASE_TAG)
	docker push $(DOCKER_REGISTRY_LIVE)/api:$(RELEASE_TAG)

deploy: # Deploy artefacts - mandatory: PROFILE=[name]
	make project-deploy STACK=application PROFILE=$(PROFILE)

provision-plan:
	make terraform-plan STACK=$(INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)

provision: # Provision environment - mandatory: PROFILE=[name]
	make terraform-apply-auto-approve STACK=$(INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)

provision-sns-plan:
	make terraform-plan STACK=$(SNS_INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)

provision-sns: # Provision environment - mandatory: PROFILE=[name]
	make terraform-apply-auto-approve STACK=$(SNS_INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)

clean: # Clean up project
	make stop
	docker network rm $(DOCKER_NETWORK) 2> /dev/null ||:

run-smoke-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-smoke COGNITO_USER_PASS=$$(make aws-secret-get NAME=$(PROJECT_GROUP_SHORT)-sfsa-${PROFILE}-cognito-passwords | jq .POSTCODE_PASSWORD | tr -d '"')

run-contract-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-contract COGNITO_USER_PASS=$$(make aws-secret-get NAME=$(PROJECT_GROUP_SHORT)-sfsa-${PROFILE}-cognito-passwords | jq .POSTCODE_PASSWORD | tr -d '"')

run-jmeter-performance-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter ACCESS_TOKEN=$$(make -s extract-access-token) JMETER_TEST_FOLDER_PATH=test/jmeter/tests/performance JMETER_TEST_FILE_PATH=test/jmeter/tests/performance/performanceTest.jmx

run-jmeter-load-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter ACCESS_TOKEN=$$(make -s extract-access-token) JMETER_TEST_FOLDER_PATH=test/jmeter/tests/load JMETER_TEST_FILE_PATH=test/jmeter/tests/load/loadTest.jmx

run-jmeter-stress-test:
	eval "$$(make aws-assume-role-export-variables)"
	make run-jmeter ACCESS_TOKEN=$$(make -s extract-access-token) JMETER_TEST_FOLDER_PATH=test/jmeter/tests/stress JMETER_TEST_FILE_PATH=test/jmeter/tests/stress/stressTest.jmx

deploy-jmeter-namespace:
	eval "$$(make aws-assume-role-export-variables)"
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	kubectl create ns ${PROJECT_ID}-${PROFILE}-jmeter
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_slaves_deploy.yaml
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_slaves_svc.yaml
	kubectl apply -n ${PROJECT_ID}-${PROFILE}-jmeter -f deployment/jmeter/jmeter_master_deploy.yaml
	make k8s-sts K8S_APP_NAMESPACE=${PROJECT_ID}-${PROFILE}-jmeter

destroy-jmeter-namespace:
	eval "$$(make aws-assume-role-export-variables)"
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	kubectl delete ns ${PROJECT_ID}-${PROFILE}-jmeter


# ==============================================================================
# Supporting targets
project-aws-get-authentication-secret: #Get AWS Pass
	aws secretsmanager get-secret-value \
		--secret-id $(PROJECT_GROUP_SHORT)-sfsa-$(ENVIRONMENT)-cognito-passwords \
		--region $(AWS_REGION) \
		--query 'SecretString' \
		--output text

extract-access-token:
	make -s get-authentication-access-token ADMIN_PASSWORD=$$(make -s project-aws-get-authentication-secret | jq .ADMIN_PASSWORD | tr -d '"') | jq .accessToken | tr -d '"'

get-authentication-access-token:
		curl --request POST ${AUTHENTICATION_ENDPOINT} \
			--header 'Content-Type: application/json' \
			--data-raw '{"emailAddress": "service-finder-admin@nhs.net","password": "${ADMIN_PASSWORD}"}'


run-jmeter: # Run jmeter tests - mandatory: JMETER_TEST_FOLDER_PATH - test directory JMETER_TEST_FILE_PATH - the path of the jmeter tests to run
	sed -i 's|ACCESS_TOKEN_TO_REPLACE|$(ACCESS_TOKEN)|g' ${JMETER_TEST_FILE_PATH}
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	kubectl config set-context --current --namespace=${PROJECT_ID}-${PROFILE}-jmeter
	test/jmeter/scripts/jmeter_stop.sh
	test/jmeter/scripts/start_test.sh ${JMETER_TEST_FOLDER_PATH} ${JMETER_TEST_FILE_PATH}

trust-certificate: ssl-trust-certificate-project ## Trust the SSL development certificate

docker-run-mvn-lib-mount: ### Build Docker image mounting library volume - mandatory: DIR, CMD
	make docker-run-mvn LIB_VOLUME_MOUNT=true \
		DIR="$(DIR)" \
		CMD="$(CMD)"

# ==============================================================================
# Pipeline targets

build-artefact:
	echo TODO: $(@)

publish-artefact:
	echo TODO: $(@)

backup-data:
	echo TODO: $(@)

provision-infractructure:
	echo TODO: $(@)

deploy-artefact:
	echo TODO: $(@)

apply-data-changes:
	echo TODO: $(@)S

# --------------------------------------

run-static-analisys:
	echo TODO: $(@)

run-unit-test:
	echo TODO: $(@)

run-contract:
	sed -i -e 's|SECRET_TO_REPLACE|$(COGNITO_USER_PASS)|g' $(APPLICATION_TEST_DIR)/contract/environment/postcode_contract.postman_environment.json
	make stop
	make start PROFILE=local
	make docker-run-postman \
		DIR="$(APPLICATION_TEST_DIR)/contract" \
		CMD=" \
			run PostcodeAPIContractTests.postman_collection.json -e environment/postcode_contract.postman_environment.json --verbose --insecure \
		"
	make project-stop

run-smoke:
	sed -i -e 's|SECRET_TO_REPLACE|$(COGNITO_USER_PASS)|g' $(APPLICATION_TEST_DIR)/contract/environment/postcode_smoke.postman_environment.json
	make restart
	make docker-run-postman \
		DIR="$(APPLICATION_TEST_DIR)/contract" \
		CMD=" \
			run PostcodeAPISmokeTests.postman_collection.json -e environment/postcode_smoke.postman_environment.json --verbose --insecure \
		"
	make project-stop

run-integration-test:
	echo TODO: $(@)

run-functional-test:
	[ $$(make project-branch-func-test) != true ] && exit 0
	echo TODO: $(@)

run-performance-test:
	[ $$(make project-branch-perf-test) != true ] && exit 0
	echo TODO: $(@)

run-security-test:
	[ $$(make project-branch-sec-test) != true ] && exit 0
	echo TODO: $(@)

# --------------------------------------

remove-unused-environments:
	echo TODO: $(@)

remove-old-artefacts:
	echo TODO: $(@)

remove-old-backups:
	echo TODO: $(@)

# --------------------------------------

pipeline-finalise: ## Finalise pipeline execution - mandatory: PIPELINE_NAME,BUILD_STATUS
	# Check if BUILD_STATUS is SUCCESS or FAILURE
	make pipeline-send-notification

pipeline-send-notification: ## Send Slack notification with the pipeline status - mandatory: PIPELINE_NAME,BUILD_STATUS
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make secret-fetch-and-export-variables NAME=$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)/deployment)"
	make slack-it

# --------------------------------------

pipeline-check-resources: ## Check all the pipeline deployment supporting resources - optional: PROFILE=[name]
	profiles="$$(make project-list-profiles)"
	# for each profile
	#export PROFILE=$$profile
	# TODO:
	# table: $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-deployment
	# secret: $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)/deployment
	# bucket: $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)-deployment
	# certificate: SSL_DOMAINS_PROD
	# repos: DOCKER_REPOSITORIES

pipeline-create-resources: ## Create all the pipeline deployment supporting resources - optional: PROFILE=[name]
	profiles="$$(make project-list-profiles)"
	# for each profile
	#export PROFILE=$$profile
	# TODO:
	# Per AWS accoount, i.e. `nonprod` and `prod`
	eval "$$(make aws-assume-role-export-variables)"
	#make aws-dynamodb-create NAME=$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-deployment ATTRIBUTE_DEFINITIONS= KEY_SCHEMA=
	#make secret-create NAME=$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)/deployment VARS=DB_PASSWORD,SMTP_PASSWORD,SLACK_WEBHOOK_URL
	#make aws-s3-create NAME=$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)-deployment
	#make ssl-request-certificate-prod SSL_DOMAINS_PROD
	# Centralised, i.e. `mgmt`
	eval "$$(make aws-assume-role-export-variables AWS_ACCOUNT_ID=$(AWS_ACCOUNT_ID_MGMT))"
	#make docker-create-repository NAME=NAME_TEMPLATE_TO_REPLACE
	#make aws-codeartifact-setup REPOSITORY_NAME=$(PROJECT_GROUP_SHORT)

# ==============================================================================

derive-build-tag:
	dir=$$(make _docker-get-dir NAME=dos-postcode-api)
	echo $$(cat $$dir/VERSION) | \
				sed "s/YYYY/$$(date --date=$(BUILD_DATE) -u +"%Y")/g" | \
				sed "s/mm/$$(date --date=$(BUILD_DATE) -u +"%m")/g" | \
				sed "s/dd/$$(date --date=$(BUILD_DATE) -u +"%d")/g" | \
				sed "s/HH/$$(date --date=$(BUILD_DATE) -u +"%H")/g" | \
				sed "s/MM/$$(date --date=$(BUILD_DATE) -u +"%M")/g" | \
				sed "s/ss/$$(date --date=$(BUILD_DATE) -u +"%S")/g" | \
				sed "s/SS/$$(date --date=$(BUILD_DATE) -u +"%S")/g" | \
				sed "s/hash/$$(git rev-parse --short HEAD)/g"

# ==============================================================================

.SILENT: \
	derive-build-tag
