pipeline {
    agent any

    stages {
        stage('Set Variables') {
            steps {
                echo 'Set Variables'
                script {
                    // BASIC
                    REPOSITORY_URL = 'https://github.com/f-lab-edu/rocket-market.git'

                    // DOCKER
                    DOCKER_HUB_URL = 'registry.hub.docker.com'
                    DOCKER_HUB_FULL_URL = 'https://' + DOCKER_HUB_URL
                    DOCKER_HUB_CREDENTIAL_ID = 'docker-hub-credentials-id'
                    DOCKER_IMAGE_NAME = 'rocket-market-api'
                    DOCKER_IMAGE_TAG = 'latest'
                }
            }
        }

        stage('Git Checkout') {
            steps {
                echo 'Checkout Remote Repository'
                git branch: "${env.BRANCH_NAME}",
                url: REPOSITORY_URL
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./gradlew'
                echo 'Build With gradlew'
                sh './gradlew clean build'
            }
            post {
                failure {
                  error '[Build] This pipeline stops here...'
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                echo 'Build & Push Docker Image'
                withCredentials([usernamePassword(
                        credentialsId: DOCKER_HUB_CREDENTIAL_ID,
                        usernameVariable: 'DOCKER_HUB_ID',
                        passwordVariable: 'DOCKER_HUB_PW')]) {

                    script {
                        docker.withRegistry(DOCKER_HUB_FULL_URL, DOCKER_HUB_CREDENTIAL_ID) {
                            app = docker.build(DOCKER_HUB_ID + '/' + DOCKER_IMAGE_NAME)
                            app.push(DOCKER_IMAGE_TAG)
                        }
                    }
                }
            }
            post {
                 failure {
                   error '[Docker Image] This pipeline stops here...'
                 }
             }
        }

         stage('Deploy with Docker Compose') {
             steps {
                    echo 'Deploy with Docker Compose'
                     sh 'docker compose pull'
                     sh 'docker compose up -d'
                 }
             post {
                 failure {
                   error '[Deploy] This pipeline stops here...'
                 }
             }
         }
    }

    post {
        success {
            echo 'Deployment succeeded!'
        }
        failure {
            echo 'Deployment failed.'
        }
        always {
            cleanWs()
        }
    }
}
