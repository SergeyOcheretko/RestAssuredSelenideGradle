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
               echo '🧼 Удаляем завершённые контейнеры (Windows CMD)...'
               bat '''
               FOR /F "tokens=*" %%i IN (`docker ps -a --filter "status=exited" -q`) DO docker rm %%i
               '''
           }
       }


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
                    Write-Error "Grid не стал ready за 60 с"
                    exit 1
                    '''
                }
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
                '''
            }
        }

        stage('UI Tests') {
            steps {
                echo '🧪 Запускаем UI тесты на Grid...'
                bat '''
                call .\\gradlew uiTest --console=plain --no-daemon ^
                -Dwebdriver.remote.url=%GRID_URL% ^
                --gradle-user-home=%GRADLE_USER_HOME%
                '''
            }
        }

        stage('API Tests') {
            steps {
                echo '🌐 Запускаем API тесты...'
                bat 'call .\\gradlew apiTest --console=plain --no-daemon --gradle-user-home=%GRADLE_USER_HOME%'
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
