pipeline {
  /*
    Description: Development pipeline to build test push and deploy to nonprod
   */
  agent {
    label 'jenkins-slave'
  }

  environment {
    PROFILE = 'dev'
  }

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: 'MINUTES')
  }

  triggers { pollSCM('* * * * *') }

  stages {
    stage('Prepare for jenkins-slave run') {
      steps {
        script {
          sh "make pipeline-slave-prepare"
        }
      }
    }
    stage('Prepare') {
        steps {
            sh 'make prepare'
        }
    }
    stage('Show Variables') {
      steps {
        script {
          sh 'make devops-print-variables'
        }
      }
    }
    stage('Scan for Secrets') {
      steps {
        script {
          sh 'make pipeline-secret-scan'
        }
      }
    }
    stage('Derive Build Tag') {
      steps {
        script {
          env.PROJECT_BUILD_TAG = sh(returnStdout: true, script: 'make derive-build-tag').trim()
        }
      }
    }
    stage('Scan Dependencies') {
      steps {
        script {
          sh 'make scan'
        }
        archiveArtifacts artifacts: 'reports/**'
      }
    }
    stage('Build API') {
      steps {
        script {
          sh "make build VERSION=${env.PROJECT_BUILD_TAG}"
        }
      }
    }
    stage('Unit Test') {
      steps {
        script {
          sh 'make unit-test'
        }
      }
    }
    stage('Run Contract Tests') {
      steps {
        script {
          sh 'make run-contract-test'
          publishHTML([allowMissing: false,
            alwaysLinkToLastBuild: false,
            keepAll: true,
            reportDir: 'test/contract/reports/',
            reportFiles: 'PostcodeAPIContractTests.html',
            reportName: 'Postcode API Contract Report',
            reportTitles: 'Postcode API Contract Report'
          ])
        }
      }
    }
    stage('Push API Image to ECR') {
      steps {
        script {
          sh "make push VERSION=${env.PROJECT_BUILD_TAG}"
        }
      }
    }
    stage('Image Build Tag') {
      steps {
        script {
          sh "echo 'Image Build Tag: '${env.PROJECT_BUILD_TAG}"
        }
      }
    }
  }
  post {
    always { sh 'make clean' }
  }
}
