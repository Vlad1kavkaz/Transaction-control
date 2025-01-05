pipeline {
    agent any

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: 'Branch to build from')
    }

    environment {
        REPO_URL = 'https://github.com/Vlad1kavkaz/Transaction-control.git'
        DOCKER_IMAGE = 'gateway'
        NETWORK_NAME = 'txn_control_jenkins_network'
        PORT_MAPPING = '8080:8080'
        SERVICE_NAME = 'gateway'
    }

    stages {
        stage('Checkout Repository') {
            steps {
                echo "Cloning repository from branch: ${BRANCH_NAME}..."
                git branch: "${BRANCH_NAME}", url: "${REPO_URL}"
            }
        }

        stage('Compile Build and Test') {
            steps {
                echo "Compiling and building the project in ${SERVICE_NAME}..."
                sh "cd ${SERVICE_NAME} && mvn clean package"
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image for ${SERVICE_NAME}..."
                sh "docker build -t ${DOCKER_IMAGE} ${SERVICE_NAME}"
            }
        }

        stage('Redeploy Docker Container') {
            when {
                expression { params.BRANCH_NAME == 'master' }
            }
            steps {
                script {
                    def containerExists = sh(script: "docker ps -q -f name=${DOCKER_IMAGE}", returnStdout: true).trim()

                    if (containerExists) {
                        echo "Stopping and removing existing container ${DOCKER_IMAGE}..."
                        sh "docker stop ${DOCKER_IMAGE}"
                        sh "docker rm ${DOCKER_IMAGE}"
                    }

                    echo "Running new Docker container for ${SERVICE_NAME} on branch ${BRANCH_NAME}..."
                    sh "docker run -d --name ${DOCKER_IMAGE} --network ${NETWORK_NAME} -p ${PORT_MAPPING} ${DOCKER_IMAGE}"
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
            sh "docker rm -f ${DOCKER_IMAGE}"
        }
    }
}
