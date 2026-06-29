pipeline {
    agent any

    tools {
        maven 'Maven3'   // 對應 Jenkins → Tools → Maven installations 的 Name
    }

    environment {
        VM_HOST    = '10.10.3.134'
        DEPLOY_DIR = '/home/vboxuser/app'
        SSH_CRED   = 'vm-ssh-password'
    }

    stages {

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    bat 'npm ci'
                    bat 'npm run build'
                }
            }
        }

        stage('Build Backend JAR') {
            steps {
                dir('backend') {
                    bat 'mvn clean package -DskipTests'
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
                    bat "plink -ssh -pw %SSHPASS% -batch %SSHUSER%@%VM_HOST% \"mkdir -p %DEPLOY_DIR%/data\""
                    bat "pscp -pw %SSHPASS% -batch backend\\target\\permission-system.jar %SSHUSER%@%VM_HOST%:%DEPLOY_DIR%/permission-system.jar"
                    bat "plink -ssh -pw %SSHPASS% -batch %SSHUSER%@%VM_HOST% \"sudo systemctl restart permission-system\""
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
                    bat "plink -ssh -pw %SSHPASS% -batch %SSHUSER%@%VM_HOST% \"sudo systemctl is-active permission-system\""
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
