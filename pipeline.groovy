pipeline {
    agent any
    environment {
        PROJECT_ID = 'your-gcp-project-id'
        CLUSTER_NAME = 'my-gke-cluster'
        REGION = 'us-central1'
        BLUE_DEPLOYMENT = 'blue-deployment.yaml'
        GREEN_DEPLOYMENT = 'green-deployment.yaml'
        SERVICE = 'service.yaml'
    }
    stages {
        stage('Setup Kubernetes') {
            steps {
                sh 'gcloud container clusters get-credentials $CLUSTER_NAME --region=$REGION --project=$PROJECT_ID'
            }
        }
        stage('Deploy Blue App') {
            steps {
                sh 'kubectl apply -f $BLUE_DEPLOYMENT'
                sh 'kubectl apply -f $SERVICE'
            }
        }
        stage('Deploy Green App (Blue-Green Transition)') {
            steps {
                sh 'kubectl apply -f $GREEN_DEPLOYMENT'
                sleep(time: 10, unit: 'SECONDS') // Wait for pods to spin up
                sh 'kubectl delete -f $BLUE_DEPLOYMENT'
            }
        }
    }
    post {
        always {
            sh 'kubectl get all'
        }
    }
}
