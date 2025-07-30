pipeline {
    agent any

    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        GRID_STATUS_URL = 'http://localhost:4444/status'
    }

    stages {

        stage('Cleanup Docker') {
            steps {
                echo '🧼 Удаляем завершённые контейнеры...'
                powershell '''
                    docker container prune -f
                '''
            }
        }

        stage('Detect Port Conflict') {
            steps {
                echo '🔎 Проверяем занятость порта 4444...'
                powershell '''
                    $used = netstat -an | findstr ":4444"
                    $log = "Порт 4444 " + ($used ? "занят" : "свободен")
                    $log | Out-File "build/port-check.log"

                    if ($used) {
                        "❌ Порт 4444 занят. Перезапускаем Grid..." | Out-File "build/grid-recovery.log"
                        docker ps -q --filter "ancestor=selenium/hub" | ForEach-Object { docker stop $_; docker rm $_ }
                        docker compose -f docker-compose.yml up -d
                        Start-Sleep -Seconds 10
                    }
                '''
                archiveArtifacts artifacts: 'build/*.log', allowEmptyArchive: true
            }
        }

        stage('Wait Grid Ready') {
            steps {
                echo '⏳ Ожидаем готовность Selenium Grid...'
                powershell '''
                    $tries = 0
                    do {
                        Start-Sleep -Seconds 10
                        try {
                            $response = Invoke-RestMethod -Uri "http://localhost:4444/status"
                        } catch {
                            $response = @{ value = @{ ready = $false } }
                        }
                        $tries++
                    } while ($response.value.ready -ne $true -and $tries -lt 5)

                    if ($response.value.ready -ne $true) {
                        "❌ Grid не стал ready за $(10 * $tries) секунд" | Out-File "build/grid-fail.log"
                        exit 0  # Мягкое завершение, без падения пайплайна
                    } else {
                        "✅ Grid готов через $(10 * $tries) секунд" | Out-File "build/grid-ready.log"
                    }
                '''
                archiveArtifacts artifacts: 'build/grid-*.log', allowEmptyArchive: true
            }
        }

        stage('Clean Build') {
            steps {
                echo '🧹 Чистим предыдущую сборку...'
                bat 'gradlew clean'
            }
        }

        stage('UI Tests') {
            steps {
                echo '🎯 Запускаем UI тесты...'
                bat 'gradlew testUI'
            }
        }

        stage('API Tests') {
            steps {
                echo '🌐 Запускаем API тесты...'
                bat 'gradlew testAPI'
            }
        }

        stage('Allure Report') {
            steps {
                echo '📊 Генерируем Allure отчёт...'
                bat 'gradlew allureReport'
            }
        }

        stage('Publish Report') {
            steps {
                echo '📦 Архивируем JUnit, Allure HTML отчёт и docker логи...'
                junit '**/build/test-results/**/*.xml'
                archiveArtifacts artifacts: '**/build/reports/**, build/docker-logs.txt', allowEmptyArchive: true

                bat 'docker compose -f docker-compose.yml logs > build/docker-logs.txt'
            }
        }

        stage('Stop Grid') {
            steps {
                echo '🧹 Останавливаем Selenium Grid...'
                bat 'docker compose -f docker-compose.yml down --remove-orphans || exit 0'
            }
        }
    }

    post {
        always {
            echo '🪶 Завершение пайплайна...'
            archiveArtifacts artifacts: 'build/*.log', allowEmptyArchive: true
        }
        failure {
            echo '❗ Пайплайн завершён с ошибкой'
        }
        success {
            echo '✅ Пайплайн завершён успешно'
        }
    }
}
