pipeline {
    agent any

    environment {
        // Phase 1: Docker Hub. Phase 2: swap to ECR repo URI.
        DOCKER_IMAGE   = "harshadocker13/pipeline-lab"
        IMAGE_TAG      = "${env.BUILD_NUMBER}"
        DOCKERHUB_CRED = "dockerhub-creds"   // Jenkins credential ID, configure in Manage Jenkins > Credentials
        
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Harshahub/Javaspringdemo.git'
            }
        }

        //stage('Build with Maven') {
          //  steps {
            //    sh 'mvn clean package -DskipTests'
            //}
        //}
		stage('Build') {
		environment {
        // This targets the JDK 17 compiler you just installed
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
		}
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
                sshagent(credentials: ['minikube-ssh-key']) {
                    sh """
                        sed -i 's|IMAGE_PLACEHOLDER|${DOCKER_IMAGE}:${IMAGE_TAG}|' k8s/deployment.yaml
                        scp -o StrictHostKeyChecking=no k8s/deployment.yaml k8s/service.yaml ubuntu@172.31.84.243:/home/ubuntu/
                        ssh -o StrictHostKeyChecking=no ubuntu@172.31.84.243 'kubectl apply -f /home/ubuntu/deployment.yaml -f /home/ubuntu/service.yaml && kubectl rollout status deployment/pipeline-lab --timeout=120s'
                    """
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
