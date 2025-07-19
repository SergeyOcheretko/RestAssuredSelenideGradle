pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Получаем код из репозитория...'
                checkout scm
            }
        }

        stage('UI Tests') {
            steps {
                echo '🧪 Запускаем UI тесты...'
                sh './gradlew clean test -DincludeTags=ui'
            }
        }

        stage('API Tests') {
            steps {
                echo '🌐 Запускаем API тесты...'
                sh './gradlew test -DincludeTags=api'
            }
        }

        stage('Smoke Tests') {
            steps {
                echo '🚬 Smoke-прогон...'
                sh './gradlew smokeTest'
            }
        }

        stage('Regression Tests') {
            steps {
                echo '🔁 Regression-прогон...'
                sh './gradlew regressionTest'
            }
        }

        stage('Allure Report') {
            steps {
                echo '📊 Генерируем Allure отчёт...'
                sh './gradlew allureReport'
            }
        }

        stage('Publish Report') {
            steps {
                allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/test/*.xml'
        }
    }
}
