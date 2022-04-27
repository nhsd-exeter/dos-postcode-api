pipeline {
  /*
    Description: Deployment pipeline
   */

  agent { label 'jenkins-slave' }

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 3.5, unit: 'HOURS')
  }

  environment {
    PROFILE = 'perf'
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

    stage('Plan Infrastructure') {
      steps {
        script {
          sh "make provision-plan PROFILE=${env.PROFILE}"
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
          sh "make monitor-r53-connection PROFILE=${env.PROFILE}"
        }
      }
    }
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
    stage('Deploy jMeter') {
        steps {
          script {
            sh "make deploy-jmeter-namespace PROFILE=${env.PROFILE}"
          }
        }
    }
    stage('Nominal Test') {
      agent {
        label 'jenkins-slave'
      }
      steps {
        script {
          sh "make run-jmeter-nominal-test PROFILE=${env.PROFILE}"
        }
      }
      post {
        always {
          archiveArtifacts artifacts: 'nominal-test-results/**'
        }
      }
    }
    stage('Peak Test') {
      agent {
        label 'jenkins-slave'
      }
      steps {
        script {
          sh "make run-jmeter-peak-test PROFILE=${env.PROFILE}"
        }
      }
      post {
        always {
          archiveArtifacts artifacts: 'peak-test-results/**'
        }
      }
    }
    stage('Double Peak Test') {
      agent {
        label 'jenkins-slave'
      }
      steps {
        script {
          sh "make run-jmeter-double-peak-test PROFILE=${env.PROFILE}"
        }
      }
      post {
        always {
          archiveArtifacts artifacts: 'doublepeak-test-results/**'
        }
      }
    }
    stage('Burst Norminal Test') {
      agent {
        label 'jenkins-slave'
      }
      steps {
        script {
          sh "make run-jmeter-burst-nominal-test PROFILE=${env.PROFILE}"
        }
      }
      post {
        always {
          archiveArtifacts artifacts: 'burstnominal-test-results/**'
        }
      }
    }
    stage('Burst Peak Test') {
      agent {
        label 'jenkins-slave'
      }
      steps {
        script {
          sh "make run-jmeter-burst-peak-test PROFILE=${env.PROFILE}"
        }
      }
      post {
        always {
          archiveArtifacts artifacts: 'burstpeak-test-results/**'
        }
      }
    }
    stage('Burst Double Peak Test') {
      agent {
        label 'jenkins-slave'
      }
      steps {
        script {
          sh "make run-jmeter-burst-double-peak-test PROFILE=${env.PROFILE}"
        }
      }
      post {
        always {
          archiveArtifacts artifacts: 'burstdoublepeak-test-results/**'
        }
      }
    }
  }
  post {
    always {
      script {
        try {
          sh "make destroy-jmeter-namespace PROFILE=${env.PROFILE}"
        } catch (error) {
          println 'Error happened while trying to destroy jmeter namespace, continuing'
        }
        try {
          sh "make delete-namespace PROFILE=${env.PROFILE}"
        } catch (error) {
          println 'Error happened while trying to destroy profile namespace, continuing'
        }
        try {
          sh "make destroy-infrastructure PROFILE=${env.PROFILE}"
        } catch (error) {
          println 'Error happened while tearing down profile infrastructure, continuing'
        }
      }
    }
    success { sh 'make pipeline-on-success' }
    failure {
      sh 'make pipeline-on-failure'
    }
  }
}
