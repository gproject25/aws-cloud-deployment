# ☁️ AWS Cloud 아키텍처 설계 및 배포

---

AWS 기반의 클라우드 인프라를 설계하고 Spring Boot 애플리케이션을 배포하는 실습 프로젝트!

#### 기능 구현 과정: [Notion](https://bolder-shrimp-6f4.notion.site/_-417df7a8441c80f389b6d4355f69498f?pvs=73)

#### 프로젝트 기록: [Velog](https://velog.io/@juhan913/posts)

---

### Level 0. AWS Budget 설정

![img.png](images/budget.png)
![img_1.png](images/budget2.png)

---

### Level 1. VPC 설계 + EC2 배포

**EC2 퍼블릭 IP**: 43.203.254.146
![ec2instanceIp.png](images/ec2instanceIp.png)

로컬 실행:
![img_3.png](images/localhealth.png)

EC2에서 실행:
![img_2.png](images/ec2health.png)

---

### Level 2. Actuator + RDS 보안 그룹 검증

- **Actuator Info 엔드포인트 URL**: http://43.203.254.146:8080/actuator/info
![img.png](images/ActuatorInfoEndpoint.png)

- **RDS 보안 그룹 인바운드 규칙**

    보안 그룹 체이닝 -> IP 주소를 직접 지정하는 대신, 특정 보안 그룹에 속한 서버(EC2)만 DB에 접근 가능 
![img.png](images/RDS보안그룹.png)

---

### Level 3. 프로필 사진 기능 추가 (S3)

Presigned URL: [ProfilePicURL](https://spring-cloud-gproject-files.s3.ap-northeast-2.amazonaws.com/profile-images/1/ea3fd1e0-df41-4df5-b160-4ef2bbd89b39_amongtrees-3.png?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEPr%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkcwRQIgMtXLr%2B%2BPq2xpzpOK36IuyVaeQCOgFA2Sf8%2F27QTKphoCIQCiWpVxcYkJDdpC7fA7oU9vbvxj%2FnmdBNfjTRm03lnscyrSBQjD%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDY1Nzk2MTU2MDk4NCIMOUl0mDX5nFHMvOP3KqYFu6tsqZJ0%2FTeq%2Fryuj2%2Bmntr82DJbo4HtMB622U64Xwcn7VOxyULG%2Fxl2BD6R7%2F6CTrVZqQHC7XO4px0yERvFMLYj2XNS3TIEWXwVYBauRMO1FM0%2Be3bDvoKihZ1hlQG7iV7Ri%2BOz1oHAMNNLECsMNz4y1PqnLcwqywG9LxaYzwtqgYccBfHuSzJF3LeCZvUjw0XvBVxelidooB5I%2FYtebA%2BBG4pJukET4aSm0o1xuYPcPgYlUh9O4nYS0j3TbxbN9dRAX9qJdRqx0BGEqk77NvDxFB5Tme8KBKwTPuTbmjcs1my%2FdFMKk5pO3Kg4VK0jMAap976qcHwswNLPIYePYq9LKsaUGjqqBFnOt24NrgpIWIH6%2F6q4ehVhz2XMt6PNG%2FLrQ6j3EfmcgN3WmwYsCq8UdHne9q94cTLrFDCOIokR6tiWEW32cl4q7Bl%2B3AzHpur1Ex%2BRA6QQo%2BKg5ZY8sDaI68awib6qyIcn2pTSC%2Fbhf3NsnP7aE9EaDH8azw0WiEomsEwnEh4shywLxr%2Bv0gkiL1sRfSyT23f628vlFHJo57Sova62Sx8g%2BG137T6e7Y0kr8S4UPSdA0Q4F8CKgWDMr7oimwg5NRjGwmbmlCh6DrLDuvjr6Rh%2BdOVzbZOi5DNzsTF%2FqJuHYYcmy5hLzaSB7nZIuXxVPwO7vrWoIzGaUsh4PShgu8aVznG9nUQURNZiHr7SuaFVOOxSwrRzlg6iwXIIEva8NKn6Ro2ahNSpvYfetXCNL3BRoMavZ6eN0eqc2wwk6hflWgt0r6JZ2zk17FXh9I7f9CjNrqT0EjuJ%2Fh70vl59LDUIkiNqb5TYTKz0Eas20xOYZK7BGG59iFqnI4UhlEBbUqjeq46wJtNoM07sn5pkJBN7jQ0%2FuW9Yd2X%2FAxmpMMvH6dcGOrEBQTBHoXtxAGlDctpbf5duphFGU3m5pzcZmiAlReglrLeLvLPUyv%2Fq1RL%2BNE3A7xRuGkbvaeVmyKu0n4YArLGYe1zV%2BVzoW95R9EonGxq3qoROXuH0iT3Ty021pbfXDWsxNbRDyr9BNy1MXEQklx6mer27tcrJc3QsQ3N0tr8ol8OR8hmsAkJetn7uzYxmQmJvUzbazoFSvBaLrUx8dNwzid7OijEkbYT68qACtZNRC4lU&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261116T024837Z&X-Amz-SignedHeaders=host&X-Amz-Credential=ASIAZSMMQVOMKBTWLF3E%2F20261116%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=2885e70d63c88953bcbad42e8173ca4dba6977c94566fdf1d6354dc5c7e5a02b)
![img.png](images/tree.png)

유효기간(7일): 2026-11-16 기준

EC2에서 접근 성공 (Upload, Download):
![img.png](images/profilepicupload.png)

![img_1.png](images/profilepicdownload.png)

S3 저장:
![img.png](images/S3_Bucket.png)

---

### Level 4. Docker & CI/CD 파이프라인

Github Actions Workflow
![img.png](images/CICDWorkflow.png)

EC2 Docker Container 실행 확인:
![DockerHubCD.png](images/DockerHubCD.png)

---
### Level 5. 고가용성 아키텍처

---

### Level 6. CloudFront CDN

CloudFront Image URL: https://d1t2ufqdn6a77o.cloudfront.net/profile-images/1/a1b735d5-e425-4c46-9671-124dfc65ef62_raccoon_animal_underwater_1343115_1920x1080.jpg

![img.png](images/profilepic2.png)

---