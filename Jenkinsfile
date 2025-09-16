pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "priis/finance-me"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test (Maven)') {
            steps {
                dir('app/finance-me') {   // 👈 move into the folder with pom.xml
                    sh 'mvn -q -DskipTests=false clean test'
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package JAR') {
            steps {
                dir('app/finance-me') {
                    sh 'mvn -q package -DskipTests'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:$BUILD_NUMBER -f app/finance-me/Dockerfile app/finance-me'
                sh 'docker tag $DOCKER_IMAGE:$BUILD_NUMBER $DOCKER_IMAGE:latest'
            }
        }

        stage('Push to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-cred',
                                                 usernameVariable: 'DOCKER_USER',
                                                 passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE:$BUILD_NUMBER'
                    sh 'docker push $DOCKER_IMAGE:latest'
                }
            }
        }

        stage('Deploy: Test (8081)') {
            steps {
                sh 'docker run -d -p 8081:8080 --name finance-me-test $DOCKER_IMAGE:$BUILD_NUMBER'
            }
        }

        stage('Approval for Prod') {
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
            }
        }

        stage('Deploy: Prod (8082)') {
            steps {
                sh 'docker run -d -p 8082:8080 --name finance-me-prod $DOCKER_IMAGE:$BUILD_NUMBER'
            }
        }
    }
}
