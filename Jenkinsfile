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
                bat './gradlew clean test -DincludeTags=ui'
            }
        }

        stage('API Tests') {
            steps {
                echo '🌐 Запускаем API тесты...'
                bat './gradlew test -DincludeTags=api'
            }
        }

        stage('Smoke Tests') {
            steps {
                echo '🚬 Smoke-прогон...'
                bat './gradlew smokeTest'
            }
        }

        stage('Regression Tests') {
            steps {
                echo '🔁 Regression-прогон...'
                bat './gradlew regressionTest'
            }
        }

        stage('Allure Report') {
            steps {
                echo '📊 Генерируем Allure отчёт...'
                bat './gradlew allureReport'
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
