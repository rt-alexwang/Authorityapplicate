pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        VM_HOST  = '192.168.82.115'
        SSH_CRED = 'vm-ssh-password'
    }

    stages {

        // ─────────────────────────────────────────
        // 權限申請系統
        // ─────────────────────────────────────────
        stage('permission: Build Frontend') {
            when {
                changeset "frontend/**"
            }
            steps {
                dir('frontend') {
                    bat 'npm ci'
                    bat 'npm run build'
                }
            }
        }

        stage('permission: Build Backend JAR') {
            when {
                anyOf {
                    changeset "frontend/**"
                    changeset "backend/**"
                }
            }
            steps {
                script {
                    // 若前端有改動，先把 dist 複製進 resources/static
                    if (currentBuild.changeSets.any { cs -> cs.items.any { it.affectedFiles.any { f -> f.path.startsWith('frontend/') } } }) {
                        bat 'if exist backend\\src\\main\\resources\\static rmdir /s /q backend\\src\\main\\resources\\static'
                        bat 'xcopy /e /i /q frontend\\dist backend\\src\\main\\resources\\static\\'
                    }
                }
                dir('backend') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('permission: Deploy') {
            when {
                anyOf {
                    changeset "frontend/**"
                    changeset "backend/**"
                }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${SSH_CRED}",
                    usernameVariable: 'SSHUSER', passwordVariable: 'SSHPASS'
                )]) {
                    bat "\"C:\\Program Files\\PuTTY\\plink.exe\"  -ssh -pw %SSHPASS% -batch %SSHUSER%@%VM_HOST% \"mkdir -p /home/vboxuser/app\""
                    bat "\"C:\\Program Files\\PuTTY\\pscp.exe\"   -pw %SSHPASS% -batch backend\\target\\permission-system-1.0.0.jar %SSHUSER%@%VM_HOST%:/home/vboxuser/app/permission-system.jar"
                    bat "\"C:\\Program Files\\PuTTY\\plink.exe\"  -ssh -pw %SSHPASS% -batch %SSHUSER%@%VM_HOST% \"sudo systemctl restart permission-system\""
                }
            }
        }

        stage('permission: Verify') {
            when {
                anyOf {
                    changeset "frontend/**"
                    changeset "backend/**"
                }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${SSH_CRED}",
                    usernameVariable: 'SSHUSER', passwordVariable: 'SSHPASS'
                )]) {
                    bat "\"C:\\Program Files\\PuTTY\\plink.exe\" -ssh -pw %SSHPASS% -batch %SSHUSER%@%VM_HOST% \"sudo systemctl is-active permission-system\""
                }
            }
        }

    }

    post {
        success { echo '部署成功' }
        failure { echo '部署失敗，請查看 Console Output' }
    }
}
