pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/Vlad1kavkaz/Transaction-control.git'
        NETWORK_NAME = 'txn_control_jenkins_network'

        IMAGE_FIN_CORE = 'fin-core'
        PORT_MAPPING_FIN_CORE = '8085:8085'
        SERVICE_NAME_FIN_CORE = 'fin-core'

        IMAGE_HIST_GEN = 'hist-gen'
        PORT_MAPPING_HIST_GEN = '8087:8087'
        SERVICE_NAME_HIST_GEN = 'hist-gen'

        IMAGE_PERSON_REG = 'person-reg'
        PORT_MAPPING_PERSON_REG = '8084:8084'
        SERVICE_NAME_PERSON_REG = 'person-reg'
    }

    stages {
        stage('Checkout Repository') {
            steps {
                echo 'Cloning repository...'
                git branch: 'master', url: "${REPO_URL}"
            }
        }

        stage('Start Infrastructure') {
            steps {
                echo 'Starting infrastructure using docker-compose...'
                sh 'docker-compose -f docker-compose.yaml up -d'
            }
        }

        //Hist gen
        stage('Compile Build and Test HistGen') {
            steps {
                echo "Compiling and building the project in ${SERVICE_NAME_HIST_GEN}..."
                sh "cd ${SERVICE_NAME_HIST_GEN} && mvn clean package"
            }
        }

        stage('Build Docker Image HistGen') {
            steps {
                echo "Building Docker image for ${SERVICE_NAME_HIST_GEN}..."
                sh "docker build -t ${IMAGE_HIST_GEN} ${SERVICE_NAME_HIST_GEN}"
            }
        }

        stage('Run Docker Container HistGen') {
            steps {
                echo "Running Docker container for ${SERVICE_NAME_HIST_GEN}..."
                sh "docker run -d --name ${IMAGE_HIST_GEN} --network ${NETWORK_NAME} -p ${PORT_MAPPING_HIST_GEN} ${IMAGE_HIST_GEN}"
            }
        }

        //Person Reg
        stage('Compile Build and Test PersonReg') {
            steps {
                echo "Compiling and building the project in ${SERVICE_NAME_PERSON_REG}..."
                sh "cd ${SERVICE_NAME_PERSON_REG} && mvn clean package"
            }
        }

        stage('Build Docker Image PeronReg') {
            steps {
                echo "Building Docker image for ${SERVICE_NAME_PERSON_REG}..."
                sh "docker build -t ${IMAGE_PERSON_REG} ${SERVICE_NAME_PERSON_REG}"
            }
        }

        stage('Run Docker Container PersonReg') {
            steps {
                echo "Running Docker container for ${SERVICE_NAME_PERSON_REG}..."
                sh "docker run -d --name ${IMAGE_PERSON_REG} --network ${NETWORK_NAME} -p ${PORT_MAPPING_PERSON_REG} ${IMAGE_PERSON_REG}"
            }
        }

        //Fin Core
        stage('Compile and Build FinCore') {
            steps {
                echo "Compiling and building the project in ${SERVICE_NAME_FIN_CORE}..."
                sh "cd ${SERVICE_NAME_FIN_CORE} && mvn clean package"
            }
        }

        stage('Build Docker Image FinCore') {
            steps {
                echo "Building Docker image for ${SERVICE_NAME_FIN_CORE}..."
                sh "docker build -t ${IMAGE_FIN_CORE} ${SERVICE_NAME_FIN_CORE}"
            }
        }

        stage('Run Docker Container FinCore') {
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
            sh "docker rm -f ${IMAGE_HIST_GEN}"
            sh "docker rm -f ${IMAGE_FIN_CORE}"
        }
    }
}
