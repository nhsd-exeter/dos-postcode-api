pipeline {
  /*
    Description: Deployment pipeline for the Demo environment
   */

  agent { label "jenkins-slave" }

  options {
    buildDiscarder(logRotator(daysToKeepStr: "7", numToKeepStr: "13"))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: "MINUTES")
  }

  environment {
    PROFILE = "dm"
  }

  parameters {
        string(
            description: 'Enter image tag to deploy, e.g. 202103111417-e362c87',
            name: 'IMAGE_TAG',
            defaultValue: ''
        )
  }

  stages {
    stage('Show Variables') {
      steps {
        script {
          sh 'make devops-print-variables'
        }
      }
    }
    stage('Check Py Lib Folder') {
      steps {
        script {
          sh 'make create-lambda-deploy-dir'
        }
      }
    }
    stage("Plan Infrastructure") {
      steps {
        script {
          sh "make provision-plan PROFILE=${env.PROFILE}"
        }
      }
    }
        stage('Plan ETL Infrastructure') {
      steps {
        script {
          sh "make plan-etl PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Provision ETL Infrastructure') {
      steps {
        script {
          sh "make provision-etl PROFILE=${env.PROFILE}"
        }
      }
    }
    stage("Provision Infrastructure") {
      steps {
        script {
          sh "make provision PROFILE=${env.PROFILE}"
        }
      }
    }
    stage("Plan SNS Infrastructure") {
      steps {
        script {
          sh "make provision-sns-plan PROFILE=${env.PROFILE}"
        }
      }
    }
    stage("Provision SNS Infrastructure") {
      steps {
        script {
          sh "make provision-sns PROFILE=${env.PROFILE}"
        }
      }
    }
    stage("Deploy API") {
      steps {
        script {
          sh "make deploy PROFILE=${env.PROFILE} IMAGE_TAG=${IMAGE_TAG}"
        }
      }
    }
    stage("Monitor Deployment") {
      steps {
        script {
          sh 'make k8s-check-deployment-of-replica-sets'
        }
      }
    }
    stage('Monitor Route53 Connection') {
      steps {
        script {
          sh 'make monitor-r53-connection'
        }
      }
    }
    stage("Perform Extract Lambda function") {
      steps {
        script {
          sh "make postcode-extract-etl PROFILE=${env.PROFILE}"
        }
      }
    }
    stage("Perform Insert Lambda function") {
      steps {
        script {
          sh "make postcode-insert-etl PROFILE=${env.PROFILE}"
        }
      }
    }
    stage("Smoke Tests") {
      steps {
        script {
          sh "make run-smoke-test"
        }
      }
    }
  }
  post {
    always { sh "make clean" }
  }
}
