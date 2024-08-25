pipeline {
    agent any

    stages {
        stage('Set Variables') {
            steps {
                echo 'Set Variables'
                script {
                    // BASIC
                    PROJECT_NAME = 'rocket-market'
                    REPOSITORY_URL = 'https://github.com/f-lab-edu/rocket-market.git'
                    PROD_BRANCH = 'main'
                    DEV_BRANCH = 'develop'
                    FEATURE_BRANCH = 'feature/21'
                    BRANCH_NAME = env.BRANCH_NAME
                    OPERATION_ENV = BRANCH_NAME.equals(PROD_BRANCH) ? 'prod' : BRANCH_NAME.equals(DEV_BRANCH) ? 'dev' : 'feature'

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
                sh './gradlew clean build'
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
                            app.push('latest')
                        }
                    }
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
