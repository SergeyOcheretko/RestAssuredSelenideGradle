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

        stage('Cleanup Docker') {
            steps {
                echo '🧼 Удаляем завершённые контейнеры...'
                powershell '''
                docker ps -a --filter "status=exited" -q | ForEach-Object {
                    Write-Host "🧹 Удаляем контейнер $_"
                    docker rm $_
                }
                '''
            }
        }

        stage('Detect Port Conflict') {
            steps {
                echo '🔎 Проверяем занятость порта 4444...'
                powershell '''
                $used = netstat -an | findstr ":4444"
                if ($used) {
                    Write-Host "⚠️ Порт 4444 занят. Пробуем остановить контейнеры..."
                    docker ps -q --filter "ancestor=selenium/hub:4.34.0" | ForEach-Object {
                        docker stop $_
                        docker rm $_
                    }
                }
                '''
            }
        }

        stage('Stop existing Selenium Grid') {
            steps {
                echo '🛑 Останавливаем Selenium Grid (если работает)...'
                bat 'docker compose -f docker-compose.yml down --remove-orphans || echo "Нечего останавливать"'
            }
        }

        stage('Start Selenium Grid') {
            steps {
                echo '🐳 Запускаем Selenium Grid...'
                bat 'docker compose -f docker-compose.yml up -d'
            }
        }

        stage('Wait Grid Ready') {
            steps {
                echo '⏳ Ожидаем готовность Selenium Grid...'
                powershell '''
                $attempt = 0
                do {
                    $attempt++
                    try {
                        $r = Invoke-WebRequest -Uri "http://localhost:4444/wd/hub/status" -UseBasicParsing -TimeoutSec 5
                        if ($r.Content -match '"ready":true') { exit 0 }
                    } catch {}
                    Start-Sleep -Seconds 2
                } while ($attempt -lt 30)
                throw "❌ Selenium Grid не стал ready за 60 секунд"
                '''
            }
        }

        stage('Clean Build') {
            steps {
                echo '🧹 Gradle clean...'
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
                echo '🧪 UI тесты на Selenium Grid...'
                bat '''
                call .\\gradlew uiTest --console=plain --no-daemon ^
                -Dwebdriver.remote.url=%GRID_URL% ^
                --gradle-user-home=%GRADLE_USER_HOME%
                '''
            }
        }

        stage('API Tests') {
            steps {
                echo '🌐 Запуск API тестов...'
                bat 'call .\\gradlew apiTest --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%'
            }
        }

        stage('Allure Report') {
            steps {
                echo '📊 Генерация Allure отчёта...'
                bat 'call .\\gradlew allureReport --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%'
            }
        }

        stage('Publish Report') {
            steps {
                echo '📤 Публикация Allure отчёта...'
                allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            }
        }
    }

    post {
        always {
            echo '📦 Архивируем JUnit и Allure HTML отчёт...'
            junit testResults: '**/build/test-results/test/*.xml', allowEmptyResults: true, skipMarkingBuildUnstable: true
            archiveArtifacts artifacts: 'build/allure-report/**', allowEmptyArchive: true

            echo '📜 Сохраняем логи Selenium Grid...'
            bat 'docker compose -f docker-compose.yml logs > build/docker-logs.txt'
            archiveArtifacts artifacts: 'build/docker-logs.txt', allowEmptyArchive: true
        }

        cleanup {
            echo '🧹 Останавливаем Selenium Grid...'
            bat 'docker compose -f docker-compose.yml down --remove-orphans || exit 0'
        }
    }
}
