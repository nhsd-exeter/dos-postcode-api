pipeline {
  /*
    Description: Deployment pipeline
   */

  agent { label 'jenkins-slave' }

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: 'MINUTES')
  }

  environment {
    PROFILE = "perf"
  }

  parameters {
        string(
            description: 'Enter image tag to deploy, e.g. 202103111417-e362c87',
            name: 'IMAGE_TAG',
            defaultValue: 'latest'
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
    stage('Start RDS Replica Instance') {
      steps {
        script {
          sh 'make start-rds-instance'
        }
      }
    }
    stage('Provision Infrastructure') {
      steps {
        script {
          sh "make provision PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Provision SNS Infrastructure') {
      steps {
        script {
          sh "make provision-sns PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Deploy API') {
      steps {
        script {
          sh "make deploy PROFILE=${env.PROFILE} IMAGE_TAG=${IMAGE_TAG}"
        }
      }
    }
    stage('Monitor Deployment') {
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
    // TODO
    // Holding stage - Don't go past here until replica has started
    //
    stage('Perform Extract Lambda function') {
      steps {
        script {
          sh "make postcode-extract-etl PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Perform Insert Lambda function') {
      steps {
        script {
          sh "make postcode-insert-etl PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Smoke Tests') {
      steps {
        script {
          sh 'make run-smoke-test'
        }
      }
    }
    // TODO
    // run performance tests here
    //
    stage('Performance Tests') {
      stage('Deploy jMeter') {
        steps {
          script {
            sh "make deploy-jmeter-namespace PROFILE=${env.PROFILE}"
          }
        }
      }
      parallel {
        stage('Norminal Peak Test') {
          steps {
            sh "make run-jmeter-nominal-test PROFILE=${env.PROFILE}"
          }
          post {
            always {
              archiveArtifacts artifacts: 'tests-test-results/**'
            }
          }
        }
        stage('Peak Test') {
          steps {
            sh "make run-jmeter-peak-test PROFILE=${env.PROFILE}"
          }
          post {
            always {
              archiveArtifacts artifacts: 'tests-test-results/**'
            }
          }
        }
        stage('Double Peak Test') {
          steps {
            sh "make run-jmeter-double-peak-test  PROFILE=${env.PROFILE}"
          }
          post {
            always {
              archiveArtifacts artifacts: 'tests-test-results/**'
            }
          }
        }
      }
      stage('Destroy jMeter') {
        steps {
          script {
            sh "make destroy-jmeter-namespace PROFILE=${env.PROFILE}"
          }
        }
      }
    }
  }
  post {
    always { script {
        sh "make delete-namespace PROFILE=${env.PROFILE}"
        sh "make destroy-infrastructure PROFILE=${env.PROFILE}"
    }
  }
    success { sh 'make pipeline-on-success' }
    failure {
      sh "make destroy-jmeter-namespace PROFILE=${env.PROFILE}"
      sh 'make pipeline-on-failure'
    }
}
}
