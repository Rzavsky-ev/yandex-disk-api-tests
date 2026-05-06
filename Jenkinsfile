pipeline {
    agent any

    tools {
        maven 'maven-3.8+'
        jdk 'jdk17'
    }

    environment {
        YANDEX_DISK_TOKEN = credentials('yandex-disk-token')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    allure includeProperties: false,
                           results: [[path: 'target/allure-results']]
                }
            }
        }
    }
}