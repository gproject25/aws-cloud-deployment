# ☁️ AWS Cloud 아키텍처 설계 및 배포

---

AWS 기반의 클라우드 인프라를 설계하고 Spring Boot 애플리케이션을 배포하는 실습 프로젝트!

#### 기능 구현 과정 및 트러블슈팅: [Notion](https://bolder-shrimp-6f4.notion.site/_-417df7a8441c80f389b6d4355f69498f?pvs=73)

#### 프로젝트 기록: [Velog](https://velog.io/@juhan913/posts)

---

![img.png](images/AWS.drawio.png)

---

## 📑 API 명세서

API 명세서는 Claude Code의 `planner` 에이전트가 구현된 코드를 읽고 초안을 작성

- 작성된 명세는 PR로 올라오며, 사람이 "결정 필요" 항목을 확인하고 리뷰한 뒤 머지
- 에이전트는 `docs/api/` 안의 파일만 수정하고, 작업 규칙은 `CLAUDE.md` 와 `docs/api/README.md`를 활용


- **API 명세서 바로가기**
    - Save Member [API](./docs/api/save-member.md)
    - Get Member [API](./docs/api/get-member.md)
    - Upload Profile Image [API](./docs/api/upload-profile-image.md)
    - Get Profile Image [API](./docs/api/get-profile-image.md)

---

### Level 0. AWS Budget 설정

예상치 못한 과금을 막기 위해 AWS Budgets로 월 예산과 알림 임계값을 설정.

![img.png](images/budget.png)

사용량이 각 80%/100%기준을 넘으면 이메일로 알림을 전송.

![img_1.png](images/budget2.png)

---

### Level 1. VPC 설계 + EC2 배포

Public/Private 서브넷을 나눈 VPC를 설계하고, Public Subnet의 EC2에 Spring Boot 애플리케이션을 배포.

Spring Boot 앱은 8080 포트로 실행되며 Actuator의 `/actuator/health`로 상태를 검증.

**EC2 퍼블릭 IP**: 43.203.254.146

![ec2instanceIp.png](images/ec2instanceIp.png)

로컬(H2, local 프로필)과 EC2(MySQL, prod 프로필) 양쪽에서 같은 헬스 체크가 성공하는 것을 확인.

**로컬 실행**:

![img_3.png](images/localhealth.png)

**EC2(Prod)에서 실행**:

![img_2.png](images/ec2health.png)

---

### Level 2. Actuator + RDS 보안 그룹 검증

DB를 앱 서버와 분리해 MySQL RDS로 구성하며, DB 접속 정보와 팀 이름은 코드에 넣지 않고 Parameter Store에서 읽어오도록 설정.

팀 이름은 `/actuator/info`에서 확인.

- **Actuator Info 엔드포인트 URL**: http://43.203.254.146:8080/actuator/info

  ![img.png](images/ActuatorInfoEndpoint.png)

  (Level 5에서 EC2를 Public에서 Private Subnet으로 옮겨서 이제는 직접 접속이 안됩니다!

  Domain URL 사용: https://spring6gproject.click/actuator/info)

RDS 보안 그룹은 IP 대신 EC2의 보안 그룹만 허용하도록 성절하며, 해당 EC2에서만 DB에 접근할 수 있도록 제어.

- **RDS 보안 그룹 인바운드 규칙**

  보안 그룹 체이닝 -> IP 주소를 직접 지정하는 대신, 특정 보안 그룹에 속한 서버(EC2)만 DB에 접근 가능

![img.png](images/RDS보안그룹.png)

---

### Level 3. 프로필 사진 기능 추가 (S3)

회원 프로필 사진을 S3에 저장하는 기능을 추가.

버킷은 퍼블릭 액세스를 전부 차단했고, EC2에는 IAM Role을 연결해 코드에 액세스 키를 넣지 않는 구조로 구현.

업로드와 조회는 `POST/GET /api/members/{id}/profile-image`로 처리하며, 조회는 유효기간 7일의 Presigned URL로 제공. 사진을 다시 올리면 기존 이미지가 S3에 남지 않도록
이전 파일은 삭제.

Presigned
URL: [ProfilePicURL](https://spring-cloud-gproject-files.s3.ap-northeast-2.amazonaws.com/profile-images/1/8cc7c92e-e0dd-4e18-a9d3-f5994da071f5_amongtrees-3.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261123T015115Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAZSMMQVOMHFKA6IUM%2F20261123%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=8bc733a1346da4d950857e5bc57f11e664877338a902412d61992a4d4aaa481e)

![img.png](images/presignedURL.png)

![img.png](images/tree.png)

유효기간(7일): 2026-11-16 기준

EC2에서 접근 성공 (Upload, Download):

![img.png](images/profilepicupload.png)

![img_1.png](images/profilepicdownload.png)

S3 저장:

![img.png](images/S3_Bucket.png)

---

### Level 4. Docker & CI/CD 파이프라인

앱을 Dockerfile로 컨테이너에서 관리하고, GitHub Actions로 `main` 브랜치에 push되면 빌드와 테스트 후 Docker Hub에 이미지를 올리고 EC2에 배포하도록 자동화.

**Github Actions Workflow**

![img.png](images/CICDWorkflow.png)

**EC2 Docker Container 실행 확인**:

![DockerHubCD.png](images/DockerHubCD.png)

---

### Level 5. 고가용성 아키텍처

단일 EC2 구조를 ALB + Auto Scaling 구조로 다시 설계.

- EC2는 Private Subnet 두 곳(2a, 2b)에 분산되고, ALB가 Target Group(`/actuator/health` 헬스 체크)으로 트래픽을 전달

- Auto Scaling Group은 Launch Template과 CPU 목표 추적 정책(30%)으로 구성했고, CPU 부하를 줘서 Scale Out/In이 동작하는 것을 확인

- Route 53에서 도메인을 구매하고 ACM 와일드카드 인증서를 발급받아 HTTPS를 적용

**Launch Template User Data 스크립트:** [user-data.sh](deploy/user-data.sh)

**ALB 리스너**

ACM 와일드카드 인증서(`*.spring6gproject.click`)를 ALB의 HTTPS:443 리스너에 연결하고, HTTP:80 요청은 HTTP_301 리다이렉션을 통해 HTTPS:443으로 전환.
HTTPS 요청은 ALB에서 처리 후 Target Group(`spring-cloud-tg`)으로 전달.

![img.png](images/listener.png)

**HTTPS 적용된 도메인 URL (예시)**

- https://api.spring6gproject.click/actuator/health
- https://spring6gproject.click/api/members/1

**HTTP Redirection**

- http://spring6gproject.click/actuator/health

![img.png](images/domainTest.png)

**Target Group**

![img.png](images/tg.png)

보안 그룹은 Internet → ALB → EC2 → RDS 순서로 체이닝

---

### Level 6. CloudFront CDN

S3의 프로필 이미지를 CloudFront를 통해 제공하도록 구성 ->

이미지가 가까운 엣지 로케이션에서 캐싱되어 응답이 빨라지고, S3를 직접 노출하지 않고도 이미지를 제공할 수 있음.

**CloudFront Image**
URL: https://d1t2ufqdn6a77o.cloudfront.net/profile-images/1/a1b735d5-e425-4c46-9671-124dfc65ef62_raccoon_animal_underwater_1343115_1920x1080.jpg

**커스텀 도메인**
URL: https://images.spring6gproject.click/profile-images/1/a1b735d5-e425-4c46-9671-124dfc65ef62_raccoon_animal_underwater_1343115_1920x1080.jpg

![img.png](images/profilepic2.png)

---
