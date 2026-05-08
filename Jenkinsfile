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
                success {
                    emailext (
                        subject: "[Jenkins] yandex-disk-tests: ✅ SUCCESS",
                        body: "Все тесты прошли! Сборка: ${BUILD_URL}",
                        to: 'rzavsky.ev@gmail.com'
                    )
                }
                failure {
                    emailext (
                        subject: "[Jenkins] yandex-disk-tests: ❌ FAILURE",
                        body: "Тесты упали. Сборка: ${BUILD_URL}",
                        to: 'rzavsky.ev@gmail.com'
                    )
                }
            }
        }
    }
}