pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/Vlad1kavkaz/Transaction-control.git'
        NETWORK_NAME = 'txn_control_jenkins_network'

        IMAGE_FIN_CORE = 'fin-core'
        PORT_MAPPING_FIN_CORE = '8085:8085'
        SERVICE_NAME_FIN_CORE = 'fin-core'
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
                echo "Compiling and building the project in ${SERVICE_NAME_FIN_CORE}..."
                sh "cd ${SERVICE_NAME_FIN_CORE} && mvn clean package"
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image for ${SERVICE_NAME_FIN_CORE}..."
                sh "docker build -t ${IMAGE_FIN_CORE} ${SERVICE_NAME_FIN_CORE}"
            }
        }


        //деплой
        stage('Run Docker Container') {
            steps {
                echo "Running Docker container for ${SERVICE_NAME_FIN_CORE}..."
                sh "docker run -d --name ${IMAGE_FIN_CORE} --network ${NETWORK_NAME} -p ${PORT_MAPPING_FIN_CORE} ${IMAGE_FIN_CORE}"
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
            sh "docker rm -f ${IMAGE_FIN_CORE}"
        }
    }
}
