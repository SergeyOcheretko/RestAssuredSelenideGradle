pipeline {
    agent any

    environment {
        JAVA_TOOL_OPTIONS = '-Dfile.encoding=UTF-8'
        GRADLE_USER_HOME = 'C:\\temp\\gradle-cache'
    }

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
                bat '''
                    chcp 65001
                    gradlew clean test -DincludeTags=ui --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%
                '''
            }
        }

        stage('API Tests') {
            steps {
                echo '🌐 Запускаем API тесты...'
                bat '''
                    chcp 65001
                    gradlew test --tests apiTests.Runner.ApiRunner --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%
                '''
            }
        }

        stage('Smoke Tests') {
            steps {
                echo '🚬 Smoke-прогон...'
                bat '''
                    chcp 65001
                    gradlew clean test -DincludeTags=smoke --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%
                '''
            }
        }

        stage('Regression Tests') {
            steps {
                echo '🔁 Regression-прогон...'
                bat '''
                    chcp 65001
                    gradlew clean test -DincludeTags=regression --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%
                '''
            }
        }

        stage('Allure Report') {
            steps {
                echo '📊 Генерируем Allure отчёт...'
                bat '''
                    chcp 65001
                    gradlew allureReport --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%
                '''
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
            echo '📦 Архивируем JUnit и Allure HTML отчёт...'
            junit '**/build/test-results/test/*.xml'
            archiveArtifacts artifacts: 'build/allure-report/**', allowEmptyArchive: true
        }
    }
}
