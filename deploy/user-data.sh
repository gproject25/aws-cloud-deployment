#!/bin/bash
set -e

# Docker Hub image
DOCKER_IMAGE="gproject25/aws-cloud-deployment:latest"

# 1. Install Docker
dnf update -y
dnf install -y docker

# 2. Start Docker
systemctl enable docker
systemctl start docker

# 3. Pull the latest application image
docker pull ${DOCKER_IMAGE}

# 4. Run Spring Boot application
docker run -d \
  -p 8080:8080 \
  --restart unless-stopped \
  --name aws-cloud-deployment \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e AWS_REGION=ap-northeast-2 \
  ${DOCKER_IMAGE}

