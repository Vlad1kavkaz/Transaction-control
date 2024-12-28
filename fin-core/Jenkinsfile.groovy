pipeline {
    agent any

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: 'Branch to build and deploy')
    }

    environment {
        REPO_URL = 'https://github.com/Vlad1kavkaz/Transaction-control.git'
        DOCKER_IMAGE = 'fin-core'
        NETWORK_NAME = 'txn_control_jenkins_network'
        PORT_MAPPING = '8085:8085'
        SERVICE_NAME = 'fin-core'
    }

    stages {
        stage('Checkout Repository') {
            steps {
                echo "Cloning repository from branch: ${BRANCH_NAME}..."
                git branch: "${BRANCH_NAME}", url: "${REPO_URL}"
            }
        }

        stage('Compile and Build') {
            steps {
                echo "Compiling and building the project in ${SERVICE_NAME}..."
                sh "cd ${SERVICE_NAME} && mvn clean package"
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo "Running tests for ${SERVICE_NAME}..."
                sh "cd ${SERVICE_NAME} && mvn test"
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image for ${SERVICE_NAME}..."
                sh "docker build -t ${DOCKER_IMAGE}:${BRANCH_NAME} ${SERVICE_NAME}"
            }
        }

        stage('Deploy to Docker') {
            when {
                expression {
                    return BRANCH_NAME == 'master'
                }
            }
            steps {
                echo "Deploying Docker container for ${SERVICE_NAME}..."
                sh "docker rm -f ${DOCKER_IMAGE} || true"
                sh "docker run -d --name ${DOCKER_IMAGE} --network ${NETWORK_NAME} -p ${PORT_MAPPING} ${DOCKER_IMAGE}:${BRANCH_NAME}"
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
            sh "docker rm -f ${DOCKER_IMAGE} || true"
        }
    }
}
