# DevOps Pipeline Lab — End-to-End CI/CD Practice

**Flow:** Maven build → GitHub → Jenkins → Docker → Kubernetes (Minikube now, EKS in Phase 2)

---

## Today's Steps (Phase 1 — GitHub push)

1. Unzip/copy this folder locally or onto your EC2 box.

2. Initialize git and push to a new GitHub repo:
   ```bash
   cd devops-pipeline-lab
   git init
   git add .
   git commit -m "Initial commit: Spring Boot pipeline-lab app"
   git branch -M main
   git remote add origin https://github.com/<your-username>/devops-pipeline-lab.git
   git push -u origin main
   ```

3. Verify the Maven build works locally first (sanity check before Jenkins touches it):
   ```bash
   mvn clean package
   java -jar target/pipeline-lab.jar
   # visit http://localhost:8080  and  http://localhost:8080/api/version
   ```

---

## Phase 1 Roadmap (Minikube)

| Step | What | Where |
|---|---|---|
| 1 | Push Maven project to GitHub | ✅ today |
| 2 | Install Jenkins on a new, isolated EC2 (t3.medium, 20GB disk) | next session |
| 3 | Install plugins: Git, Maven Integration, Docker Pipeline, Kubernetes CLI | Jenkins |
| 4 | Add credentials: Docker Hub (`dockerhub-creds`), Minikube kubeconfig (`kubeconfig-file`) | Jenkins > Manage Credentials |
| 5 | Create Pipeline job pointing at this repo's `Jenkinsfile` | Jenkins |
| 6 | Run pipeline: Checkout → Build → Test → Docker Build → Push → kubectl apply | Jenkins |
| 7 | Verify pod running: `kubectl get pods`, `minikube service pipeline-lab-svc` | Minikube |

## Phase 2 Roadmap (EKS — once Phase 1 is green)

| Step | What |
|---|---|
| 1 | `eksctl create cluster --name devops-lab --region ap-south-1 --nodes 2` |
| 2 | Create ECR repo: `aws ecr create-repository --repository-name pipeline-lab` |
| 3 | Update `Jenkinsfile`: change `DOCKER_IMAGE` to ECR URI, swap Docker Hub login for `aws ecr get-login-password` |
| 4 | Update Jenkins kubeconfig credential to point at the EKS cluster (`aws eks update-kubeconfig`) |
| 5 | Change `k8s/service.yaml` type from `NodePort` to `LoadBalancer` to get a real ALB/NLB endpoint |
| 6 | Re-run pipeline — same Jenkinsfile logic, new target |
| 7 | (Stretch) Add VPC/security group review, IAM role for Jenkins service account (IRSA) |

---

## Interview Talking Points This Builds

- End-to-end CI/CD pipeline ownership (Maven → GitHub → Jenkins → Docker → K8s)
- Readiness/liveness probes and resource requests/limits (production-hardening habits)
- Local validation before cloud deployment (mature engineering practice)
- Progression from Minikube to EKS — registry swap, IAM, networking (VPC/LoadBalancer)
- Direct tie to JD asks: CI/CD, Kubernetes, containers, IaC-adjacent automation
