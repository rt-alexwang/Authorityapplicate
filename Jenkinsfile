pipeline {
    agent any

    environment {
        VM_USER    = 'vboxuser'
        VM_HOST    = '10.10.3.134'       // 改成 VM 的 IP
        DEPLOY_DIR = '/home/vboxuser/app'
        SSH_CRED   = 'vm-ssh-password'   // Jenkins Credentials 的 ID（Username with password）
    }

    stages {

        stage('Install Tools') {
            steps {
                // 確認 Jenkins agent 上有必要工具，沒有就安裝
                sh '''
                    command -v node  || (curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - && sudo apt-get install -y nodejs)
                    command -v mvn   || sudo apt-get install -y maven
                    command -v sshpass || sudo apt-get install -y sshpass
                '''
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('Copy Frontend to Backend') {
            steps {
                sh 'rm -rf backend/src/main/resources/static'
                sh 'cp -r frontend/dist backend/src/main/resources/static'
            }
        }

        stage('Build Backend JAR') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Deploy to VM') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${SSH_CRED}",
                    usernameVariable: 'SSHUSER',
                    passwordVariable: 'SSHPASS'
                )]) {
                    sh "sshpass -p '$SSHPASS' ssh -o StrictHostKeyChecking=no $SSHUSER@${VM_HOST} 'mkdir -p ${DEPLOY_DIR}/data'"
                    sh "sshpass -p '$SSHPASS' scp backend/target/permission-system.jar $SSHUSER@${VM_HOST}:${DEPLOY_DIR}/permission-system.jar"
                    sh "sshpass -p '$SSHPASS' ssh $SSHUSER@${VM_HOST} 'sudo systemctl restart permission-system'"
                }
            }
        }

        stage('Verify') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${SSH_CRED}",
                    usernameVariable: 'SSHUSER',
                    passwordVariable: 'SSHPASS'
                )]) {
                    sh "sshpass -p '$SSHPASS' ssh $SSHUSER@${VM_HOST} 'sudo systemctl is-active permission-system'"
                }
            }
        }
    }

    post {
        success {
            echo "部署成功：http://${VM_HOST}:8090/authority/"
        }
        failure {
            echo '部署失敗，請查看 Console Output'
        }
    }
}
