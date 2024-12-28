pipeline {
    agent any

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
                echo 'Cloning repository...'
                git branch: 'master', url: "${REPO_URL}"
            }
        }

        stage('Compile and Build') {
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

        stage('Run Docker Container') {
            steps {
                echo "Running Docker container for ${SERVICE_NAME}..."
                sh "docker run -d --name ${DOCKER_IMAGE} --network ${NETWORK_NAME} -p ${PORT_MAPPING} ${DOCKER_IMAGE}"
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
            sh "docker rm -f ${DOCKER_IMAGE}" // Удаляем контейнер в случае фейла
        }
    }
}
