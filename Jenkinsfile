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

         stage('Docker Run') {
             steps {
                 echo 'Pull Docker Image & Docker Image Run'
                 sshagent (credentials: ['EC2_SSH']) {
                     withCredentials([usernamePassword(
                         credentialsId: DOCKER_HUB_CREDENTIAL_ID,
                         usernameVariable: 'DOCKER_HUB_ID',
                         passwordVariable: 'DOCKER_HUB_PW')]) {

                         sh """
                             ssh -o StrictHostKeyChecking=no ubuntu@13.209.195.235 '
                             echo "$DOCKER_HUB_PW" | docker login -u "$DOCKER_HUB_ID" --password-stdin &&
                             docker pull ${DOCKER_HUB_ID}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} &&
                             docker ps -q --filter name=rocket-market-app | grep -q . && docker rm -f \$(docker ps -aq --filter name=rocket-market-app) &&
                             docker run -d --name rocket-market-app -p 8080:8080 ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}'
                         """
                     }
                 }
             }
             post {
                 failure {
                     error '[Docker Run] This pipeline stops here...'
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
