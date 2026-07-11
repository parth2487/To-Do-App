pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
        IMAGE_NAME = "pranipa12/todo-app"   // <-- replace with your Docker Hub username
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
        DB_USERNAME = "todouser"
        DB_PASSWORD = "123456"
        APP_PORT = "8081"
            NVD_API_KEY = credentials('nvd-api-key')

    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

stage('Code Coverage') {
    steps {
        sh 'mvn jacoco:report'
    }
}

stage('OWASP Scan') {
    steps {
        sh '''
        mvn org.owasp:dependency-check-maven:check \
            -DnvdApiKey=$NVD_API_KEY
        '''
    }
}

stage('Trivy Filesystem Scan') {
    steps {
        sh '''
        trivy fs \
        --severity HIGH,CRITICAL \
        --exit-code 1 .
        '''
    }
}
        // stage('Static Code Check') {
        //     steps {
        //         // Optional but recommended for production pipelines.
        //         // Fails the build on high/critical vulnerable dependencies.
        //         sh 'mvn -B org.owasp:dependency-check-maven:check || true'
        //     }
        // }

    //     stage('Build Docker Image') {
    //         steps {
    //             sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
    //         }
    //     }

    //     stage('Push Docker Image') {
    //         steps {
    //             sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
    //             sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
    //             sh "docker push ${IMAGE_NAME}:latest"
    //         }
    //     }

    //     stage('Deploy') {
    //         steps {
    //             sh """
    //                 docker pull ${IMAGE_NAME}:latest

    //                 docker stop todo-app || true
    //                 docker rm todo-app || true

    //                 # --network todo-net lets this container reach 'todo-db' by name
    //                 # (todo-db is created once via setup-postgres.sh, not recreated here)
    //                 docker run -d --name todo-app \
    //                   --network todo-net \
    //                   -p ${APP_PORT}:${APP_PORT} \
    //                   -e SPRING_PROFILES_ACTIVE=prod \
    //                   -e SERVER_PORT=${APP_PORT} \
    //                   -e DB_HOST=todo-db \
    //                   -e DB_PORT=5432 \
    //                   -e DB_NAME=tododb \
    //                   -e DB_USERNAME=${DB_USERNAME} \
    //                   -e DB_PASSWORD=${DB_PASSWORD} \
    //                   --memory="512m" \
    //                   --restart unless-stopped \
    //                   ${IMAGE_NAME}:latest
    //             """
    //         }
    //     }

    //     stage('Health Check') {
    //         steps {
    //             script {
    //                 def maxRetries = 10
    //                 def healthy = false
    //                 for (int i = 0; i < maxRetries; i++) {
    //                     def status = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/actuator/health || true", returnStdout: true).trim()
    //                     if (status == "200") {
    //                         healthy = true
    //                         break
    //                     }
    //                     sleep(5)
    //                 }
    //                 if (!healthy) {
    //                     error "Deployed container failed health check on port ${APP_PORT}"
    //                 }
    //             }
    //         }
    //     }
    // }
    }

    post {
        success {
            echo "Build #${env.BUILD_NUMBER} deployed successfully."
        }
        failure {
            echo "Build #${env.BUILD_NUMBER} failed — check console output above."
        }
        always {
            sh "docker logout || true"
        }
    }
}