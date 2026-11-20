# ☁️ AWS Cloud 아키텍처 설계 및 배포

---

AWS 기반의 클라우드 인프라를 설계하고 Spring Boot 애플리케이션을 배포하는 실습 프로젝트!

#### 기능 구현 과정: [Notion](https://bolder-shrimp-6f4.notion.site/_-417df7a8441c80f389b6d4355f69498f?pvs=73)

#### 프로젝트 기록: [Velog](https://velog.io/@juhan913/posts)

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

업로드와 조회는 `POST/GET /api/members/{id}/profile-image`로 처리하며, 조회는 유효기간 7일의 Presigned URL로 제공. 사진을 다시 올리면 기존 이미지가 S3에 남지 않도록 이전 파일은 삭제.

Presigned URL: [ProfilePicURL](https://spring-cloud-gproject-files.s3.ap-northeast-2.amazonaws.com/profile-images/1/ea3fd1e0-df41-4df5-b160-4ef2bbd89b39_amongtrees-3.png?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEPr%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkcwRQIgMtXLr%2B%2BPq2xpzpOK36IuyVaeQCOgFA2Sf8%2F27QTKphoCIQCiWpVxcYkJDdpC7fA7oU9vbvxj%2FnmdBNfjTRm03lnscyrSBQjD%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDY1Nzk2MTU2MDk4NCIMOUl0mDX5nFHMvOP3KqYFu6tsqZJ0%2FTeq%2Fryuj2%2Bmntr82DJbo4HtMB622U64Xwcn7VOxyULG%2Fxl2BD6R7%2F6CTrVZqQHC7XO4px0yERvFMLYj2XNS3TIEWXwVYBauRMO1FM0%2Be3bDvoKihZ1hlQG7iV7Ri%2BOz1oHAMNNLECsMNz4y1PqnLcwqywG9LxaYzwtqgYccBfHuSzJF3LeCZvUjw0XvBVxelidooB5I%2FYtebA%2BBG4pJukET4aSm0o1xuYPcPgYlUh9O4nYS0j3TbxbN9dRAX9qJdRqx0BGEqk77NvDxFB5Tme8KBKwTPuTbmjcs1my%2FdFMKk5pO3Kg4VK0jMAap976qcHwswNLPIYePYq9LKsaUGjqqBFnOt24NrgpIWIH6%2F6q4ehVhz2XMt6PNG%2FLrQ6j3EfmcgN3WmwYsCq8UdHne9q94cTLrFDCOIokR6tiWEW32cl4q7Bl%2B3AzHpur1Ex%2BRA6QQo%2BKg5ZY8sDaI68awib6qyIcn2pTSC%2Fbhf3NsnP7aE9EaDH8azw0WiEomsEwnEh4shywLxr%2Bv0gkiL1sRfSyT23f628vlFHJo57Sova62Sx8g%2BG137T6e7Y0kr8S4UPSdA0Q4F8CKgWDMr7oimwg5NRjGwmbmlCh6DrLDuvjr6Rh%2BdOVzbZOi5DNzsTF%2FqJuHYYcmy5hLzaSB7nZIuXxVPwO7vrWoIzGaUsh4PShgu8aVznG9nUQURNZiHr7SuaFVOOxSwrRzlg6iwXIIEva8NKn6Ro2ahNSpvYfetXCNL3BRoMavZ6eN0eqc2wwk6hflWgt0r6JZ2zk17FXh9I7f9CjNrqT0EjuJ%2Fh70vl59LDUIkiNqb5TYTKz0Eas20xOYZK7BGG59iFqnI4UhlEBbUqjeq46wJtNoM07sn5pkJBN7jQ0%2FuW9Yd2X%2FAxmpMMvH6dcGOrEBQTBHoXtxAGlDctpbf5duphFGU3m5pzcZmiAlReglrLeLvLPUyv%2Fq1RL%2BNE3A7xRuGkbvaeVmyKu0n4YArLGYe1zV%2BVzoW95R9EonGxq3qoROXuH0iT3Ty021pbfXDWsxNbRDyr9BNy1MXEQklx6mer27tcrJc3QsQ3N0tr8ol8OR8hmsAkJetn7uzYxmQmJvUzbazoFSvBaLrUx8dNwzid7OijEkbYT68qACtZNRC4lU&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261116T024837Z&X-Amz-SignedHeaders=host&X-Amz-Credential=ASIAZSMMQVOMKBTWLF3E%2F20261116%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=2885e70d63c88953bcbad42e8173ca4dba6977c94566fdf1d6354dc5c7e5a02b)

![img.png](images/presignedURL.png)

![img.png](images/tree.png)

유효기간(7일): 2026-11-16 기준


EC2에서 접근 성공 (Upload, Download):

![img.png](images/profilepicupload.png)

![img_1.png](images/profilepicdownload.png)

S3 저장:

[img.png](images/S3_Bucket.png)

---

### Level 4. Docker & CI/CD 파이프라인

앱을 Dockerfile로 컨테이너에서 관리하고, GitHub Actions로 `main` 브랜치에 push되면 빌드와 테스트 후 Docker Hub에 이미지를 올리고 EC2에 배포하도록 자동화.


**Github Actions Workflow**

![img.png](images/CICDWorkflow.png)

**EC2 Docker Container 실행 확인**:

![DockerHubCD.png](images/DockerHubCD.png)

![img.png](images/dockertest.png)

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

CloudFront Image URL: https://d1t2ufqdn6a77o.cloudfront.net/profile-images/1/a1b735d5-e425-4c46-9671-124dfc65ef62_raccoon_animal_underwater_1343115_1920x1080.jpg

![img.png](images/profilepic2.png)

---