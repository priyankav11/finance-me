pipeline {
    agent any
    tools {
        maven 'Maven-3.8.8'
    }

    environment {
        DOCKER_IMAGE = "priis/finance-me"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Check Maven') {
            steps {
                sh 'mvn -v'
            }
        }

        stage('Build & Test (Maven)') {
            steps {
                dir('app/finance-me') {
                    sh 'mvn -q -DskipTests=false clean test'
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
                withCredentials([usernamePassword(credentialsId: 'DOCKERHUB_CREDS',
                                                 usernameVariable: 'DOCKER_USER',
                                                 passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE:$BUILD_NUMBER'
                    sh 'docker push $DOCKER_IMAGE:latest'
                }
            }
        }
    }
}


