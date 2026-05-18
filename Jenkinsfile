// Требуется наличие следующих переменных в Jenkins:
// - SSH_CRED_ID - идентификатор SSH ключа
// - CRYPTO_RATES_DEPLOY_PATH - путь на сервере, куда необходимо расположить собранные проекты
// - CRYPTO_RATES_DEPLOY_HOST - IP адрес сервера, на который будут отправлены проекты
// - SSH_USER - пользователь SSH
// - SSH_PORT - порт SSH
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean bootJar --no-daemon'
            }
        }
        stage('Deploy') {
            steps {
                sshagent([env.SSH_CRED_ID]) {
                    sh "scp -P ${SSH_PORT} build/libs/crypto-rates.jar ${SSH_USER}@${CRYPTO_RATES_DEPLOY_HOST}:${CRYPTO_RATES_DEPLOY_PATH}/"
                    sh "ssh -p ${SSH_PORT} ${SSH_USER}@${CRYPTO_RATES_DEPLOY_HOST} 'cd /srv/crypto-rates && docker rollout --wait 30 --timeout 60 crypto-rates'"
                }
            }
        }
    }

    post {
        success {
            echo 'Сборка и деплой успешно завершены!'
        }
        failure {
            echo 'Ошибка при сборке или деплое.'
        }
    }
}
