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
                    DOCKER_IMAGE_NAME = 'cheoneunjeong/rocket-market-api'
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

        stage('Build Docker Image') {
            steps {
                echo 'Build Docker Image'
                script {
                    docker.build(DOCKER_IMAGE_NAME + ':' + DOCKER_IMAGE_TAG)
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo 'Push Docker Image'
                script {
                    docker.withRegistry(DOCKER_HUB_FULL_URL, DOCKER_HUB_CREDENTIAL_ID) {
                        docker.image(DOCKER_IMAGE_NAME + ':' + DOCKER_IMAGE_TAG).push(DOCKER_IMAGE_TAG)
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
