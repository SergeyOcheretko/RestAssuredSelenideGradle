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
bat '''
if exist build\\allure-results (
    del /q build\\allure-results\\*
)
'''            }
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
stage('Inject Allure Categories') {
    steps {
        echo '🧩 Подключаем categories.json для Allure...'
        writeFile file: 'build/allure-results/categories.json', text: '''
        [
          {
            "name": "Authorization Failure",
            "matchedStatuses": ["failed"],
            "matchedMessageRegex": ".*401 Unauthorized.*|.*Access denied.*|.*token.*expired.*|.*unauthenticated.*"
          },
          {
            "name": "Validation Error",
            "matchedStatuses": ["failed"],
            "matchedMessageRegex": ".*invalid.*|.*must be at least.*|.*required.*|.*Passwords do not match.*|.*cannot start or end with.*|.*Email.*not valid.*"
          },
          {
            "name": "Duplicate Data",
            "matchedStatuses": ["failed"],
            "matchedMessageRegex": ".*already exists.*|.*duplicate.*"
          },
          {
            "name": "Server Error",
            "matchedStatuses": ["broken"],
            "matchedMessageRegex": ".*500 Internal Server Error.*|.*unexpected error.*|.*exception.*"
          },
          {
            "name": "Empty Request",
            "matchedStatuses": ["failed"],
            "matchedMessageRegex": ".*empty body.*|.*missing fields.*|.*no content.*"
          },
          {
            "name": "Content-Type Mismatch",
            "matchedStatuses": ["failed"],
            "matchedMessageRegex": ".*unsupported media type.*|.*Content-Type.*not allowed.*"
          },
          {
            "name": "UI Assertion Mismatch",
            "matchedStatuses": ["failed"],
            "matchedMessageRegex": ".*expected.*but was.*|.*element not found.*|.*FlashMessage.*"
          },
          {
            "name": "Note Access Violation",
            "matchedStatuses": ["failed"],
            "matchedMessageRegex": ".*note.*belongs to another user.*|.*forbidden.*"
          }
        ]
        '''.stripIndent()
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