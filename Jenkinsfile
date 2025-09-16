pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "priyankav/finance-me"
    }

    triggers {
        pollSCM('* * * * *') // checks GitHub every minute for changes
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test (Maven)') {
            steps {
                sh 'mvn -q -DskipTests=false clean test'
                junit 'target/surefire-reports/*.xml'
            }
        }

        stage('Package JAR') {
            steps {
                sh 'mvn -q -DskipTests package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:$BUILD_NUMBER .'
                sh 'docker tag $DOCKER_IMAGE:$BUILD_NUMBER $DOCKER_IMAGE:latest'
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKERHUB_CREDS',
                  passwordVariable: 'DOCKERHUB_PASS', usernameVariable: 'DOCKERHUB_USER')]) {
                    sh 'echo $DOCKERHUB_PASS | docker login -u $DOCKERHUB_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE:$BUILD_NUMBER'
                    sh 'docker push $DOCKER_IMAGE:latest'
                }
            }
        }

        stage('Deploy: Test (8081)') {
            steps {
                sh 'docker rm -f finance-test || true'
                sh 'docker run -d --name finance-test -p 8081:8080 $DOCKER_IMAGE:$BUILD_NUMBER'
                sh 'sleep 5 && curl -sSf http://localhost:8081/actuator/health || true'
            }
        }

        stage('Approval for Prod') {
            steps {
                input message: 'Promote to PROD?', ok: 'Deploy'
            }
        }

        stage('Deploy: Prod (8082)') {
            steps {
                sh 'docker rm -f finance-prod || true'
                sh 'docker run -d --name finance-prod -p 8082:8080 $DOCKER_IMAGE:$BUILD_NUMBER'
                sh 'sleep 5 && curl -sSf http://localhost:8082/actuator/health || true'
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f || true'
            echo "Build #${env.BUILD_NUMBER} complete"
        }
    }
}
