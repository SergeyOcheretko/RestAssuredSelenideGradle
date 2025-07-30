pipeline {
    agent any

    environment {
        JAVA_TOOL_OPTIONS = '-Dfile.encoding=UTF-8'
        GRADLE_USER_HOME  = 'C:\\temp\\gradle-cache'
        GRID_URL          = 'http://localhost:4444/wd/hub'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Получаем код из репозитория...'
                checkout scm
            }
        }

        /* ===================================
           SELENIUM GRID
           =================================== */
        stage('Start Selenium Grid') {
            steps {
                script {
                    echo '🐳 Поднимаем Selenium Grid...'
                    bat 'docker compose -f docker-compose.yml up -d'
                }
            }
        }

        stage('Wait Grid Ready') {
            steps {
                script {
                    echo '⏳ Ждём регистрации нод...'
                    bat '''
                    for /l %%i in (1,1,30) do (
                      curl -s -o nul -w "%%{http_code}" %GRID_URL%/status | findstr "200" >nul && exit 0
                      timeout 2 >nul
                    )
                    '''
                }
            }
        }

        /* ============== ТЕСТЫ ============== */
        stage('Clean Build') {
            steps {
                echo '🧹 Выполняем gradle clean...'
                bat 'call .\\gradlew clean --no-daemon --gradle-user-home=%GRADLE_USER_HOME%'

                echo '🧹 Очищаем allure-results...'
                bat '''
                if exist build\\allure-results (
                    del /q build\\allure-results\\*
                )
                '''
            }
        }

        stage('UI Tests') {
            steps {
                echo '🧪 Запускаем UI тесты на Grid...'
                bat(script: '''
                    call .\\gradlew uiTest --console=plain --no-daemon ^
                    -Dwebdriver.remote.url=%GRID_URL% ^
                    --gradle-user-home=%GRADLE_USER_HOME%
                ''', returnStatus: true)
            }
        }

        stage('API Tests') {
            steps {
                echo '🌐 Запускаем API тесты...'
                bat(script: 'call .\\gradlew apiTest --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%', returnStatus: true)
            }
        }

        /* ============== ОТЧЁТЫ ============== */
        stage('Inject Allure Categories') {
            steps {
                echo '🧩 Подключаем categories.json для Allure...'
                writeFile file: 'build/allure-results/categories.json', text: '''
[... /* ваш JSON без изменений */ ...]
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
                echo '📤 Публикуем Allure отчёт...'
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
        cleanup {
            echo '🧹 Останавливаем Selenium Grid...'
            bat 'docker compose -f docker-compose.yml down --remove-orphans || exit 0'
        }
    }
}