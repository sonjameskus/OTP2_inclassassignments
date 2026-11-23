
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
        stage('Check') {
            steps {
                git 'https://github.com/sonjameskus/OTP2_inclassassignments.git'
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn clean verify'
            }
        }

        stage('JaCoCo') {
            steps {
                jacoco()
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([
                    string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')
                ]) {
                    withSonarQubeEnv("${env.SONARQUBE_SERVER}") {
                        bat """
                            sonar-scanner ^
                            -Dsonar.projectKey=otpinclass ^
                            -Dsonar.projectName=Otpinclass ^
                            -Dsonar.sources=src/main/java ^
                            -Dsonar.java.binaries=target/classes ^
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
                            -Dsonar.token=%SONAR_TOKEN%
                        """
                    }
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
                    docker.withRegistry(
                        'https://index.docker.io/v1/',
                        DOCKERHUB_CREDENTIALS_ID
                    ) {
                        docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                    }
                }
            }
        }
    }
}
