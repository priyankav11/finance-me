pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                dir('app/finance-me') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
    }
}

