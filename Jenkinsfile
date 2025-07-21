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

        stage('Clean Build') {
            steps {
                echo '🧹 Выполняем gradle clean...'
                bat 'call .\\gradlew clean --no-daemon --gradle-user-home=%GRADLE_USER_HOME%'
                echo '🧹 Очищаем allure-results...'
                bat 'del /q build\\allure-results\\*'
            }
        }

        stage('UI Tests') {
            steps {
                echo '🧪 Запускаем UI тесты...'
                bat(script: 'call .\\gradlew uiTest --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%', returnStatus: true)
            }
        }

        stage('API Tests') {
            steps {
                echo '🌐 Запускаем API тесты...'
                bat(script: 'call .\\gradlew apiTest --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%', returnStatus: true)
            }
        }

        stage('Allure Report') {
            steps {
                echo '📊 Генерируем Allure отчёт...'
                bat 'call .\\gradlew allureReport --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%'
            }
        }

        stage('Publish Report') {
            steps {
                echo '📤 Публикуем Allure отчет...'
                allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            }
        }
    }

    post {
        always {
            echo '📦 Архивируем JUnit и Allure HTML отчёт...'
            junit testResults: '**/build/test-results/test/*.xml', allowEmptyResults: true, skipMarkingBuildUnstable: true
            archiveArtifacts artifacts: 'build/allure-report/**', allowEmptyArchive: true
        }
    }
}