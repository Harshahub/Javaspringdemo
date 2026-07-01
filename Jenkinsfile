pipeline {
    agent any

    environment {
        // Phase 1: Docker Hub. Phase 2: swap to ECR repo URI.
        DOCKER_IMAGE   = "venki/pipeline-lab"
        IMAGE_TAG      = "${env.BUILD_NUMBER}"
        DOCKERHUB_CRED = "dockerhub-creds"   // Jenkins credential ID, configure in Manage Jenkins > Credentials
        KUBECONFIG_CRED = "kubeconfig-file"  // Jenkins credential ID for kubeconfig (Minikube, later EKS)
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/<your-username>/devops-pipeline-lab.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CRED}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: "${KUBECONFIG_CRED}", variable: 'KUBECONFIG')]) {
                    sh "sed -i 's|IMAGE_PLACEHOLDER|${DOCKER_IMAGE}:${IMAGE_TAG}|' k8s/deployment.yaml"
                    sh "kubectl apply -f k8s/deployment.yaml"
                    sh "kubectl apply -f k8s/service.yaml"
                    sh "kubectl rollout status deployment/pipeline-lab --timeout=120s"
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline succeeded: ${DOCKER_IMAGE}:${IMAGE_TAG} deployed to cluster."
        }
        failure {
            echo "Pipeline failed. Check stage logs above."
        }
    }
}
