pipeline {
    agent any
    tools {
        maven 'Maven3'
    }

    environment {
        PATH = "C:\\Program Files\\DockerDesktop\\resources\\bin;${env.PATH}"
        SONARQUBE_SERVER = 'SonarQubeServer'
        DOCKERHUB_CREDENTIALS_ID = 'docker'
        DOCKERHUB_REPO = 'sinyalohis/otp2_inclass'
        DOCKER_IMAGE_TAG = 'v1'
    }
    stages {
        stage ('check'){
            steps{
                git 'https://github.com/sonjameskus/OTP2_inclassassignments.git'
            }
        }
        stage ('build'){
            steps{
                bat 'mvn clean install'
            }
        }

        stage('test') {
            steps{
                bat 'mvn test'
            }
        }
        stage('jacoco'){
            steps{
                jacoco()
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv("${env.SONARQUBE_SERVER}") {
                        bat """
                            sonar-scanner ^
                            -Dsonar.projectKey=otpinclass ^
                            -Dsonar.sources=src/main/java ^
                            -Dsonar.projectName=Otpinclass ^
                            -Dsonar.host.url=http://localhost:9000 ^
                            -Dsonar.token=%SONAR_TOKEN% ^
                            -Dsonar.java.binaries=target/classes
                            """
                        }
                    }
                }


        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }


        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                        docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                    }
                }
            }
        }

    }
}