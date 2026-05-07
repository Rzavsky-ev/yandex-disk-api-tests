pipeline {
    agent any

    environment {
        YANDEX_DISK_TOKEN = credentials('yandex-disk-token')
    }

    stages {
        stage('Test') {
            steps {
                checkout scm
                sh './mvnw clean test'
            }
              post {
                           always {
                               allure includeProperties: false,
                                      commandline: 'allure',
                                      results: [[path: 'target/allure-results']]
                           }
                       }
        }
    }
}