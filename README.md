# SpotOn 🏆  

**스포츠 경기 일정 및 커뮤니티 플랫폼**  

[![Website](https://img.shields.io/badge/website-online-brightgreen)](https://onspoton.com/)  
[![Postman API](https://img.shields.io/badge/API%20Docs-Postman-orange)](https://documenter.getpostman.com/view/38853291/2sAYQWLtf7)  
[![ERD Diagram](https://img.shields.io/badge/ERD-Diagram-blue)](https://www.erdcloud.com/d/9kNb3ACqSjytwmne3)  

---

## 🏗️ 프로젝트 개요  

**SpotOn**은 스포츠 팬들을 위한 플랫폼으로, 경기 일정 확인부터 실시간 응원 채팅, 커뮤니티 활동까지 한 곳에서 즐길 수 있는 서비스입니다.  

🔗 **서비스 URL**: [https://onspoton.com/](https://onspoton.com/)  

---

## 🛠️ 기술 스택  

### 🥇 Backend & Data Engineering
- **Spring Boot** - 애플리케이션 서버
- **MySQL** - 애플리케이션 서버 DB
- **Airflow** - 스케줄링
- **JPA, QueryDSL, JDBC** - 데이터베이스 접근
- **Redis** - 캐싱 및 세션 관리
- **JWT** - 인증 및 보안
- **STOMP** - 실시간 채팅

### 🥈 Frontend
- **React** - 사용자 인터페이스

### 🥉 DevOps
- **AWS (EC2, ECR, S3, RDS, Route53, CloudFront, IAM, ACM)** - 클라우드 환경
- **Nginx, Certbot** - 리버스 프록시 및 SSL 인증
- **Docker** - 컨테이너화
- **GitHub Actions** - CI/CD 자동화

---

## ✨ 주요 기능  

1️⃣ **경기 일정 및 결과 확인**  
   - 다양한 스포츠 경기 일정을 확인하고, 경기 결과를 실시간으로 확인

2️⃣ **실시간 경기 응원 채팅**  
   - STOMP 기반의 웹소켓을 활용한 실시간 N:N 응원 채팅 기능 제공

3️⃣ **마이팀 설정**  
   - 사용자가 응원하는 팀을 선택하면, 해당 팀의 경기 일정이 한눈에 보이도록 구성

4️⃣ **커뮤니티 게시판**  
   - 스포츠 팬들끼리 자유롭게 의견을 나누고 소통할 수 있는 공간

5️⃣ **무료 굿즈 나눔 서비스**  
   - 팬들 간의 교류 활성화를 위한 굿즈 나눔 서비스

6️⃣ **유저 간 1:1 채팅**  
   - 굿즈 무료 나눔 성사를 위한 1:1 채팅 기능 제공

---

## 📌 아키텍처  

아래 이미지는 SpotOn의 전체 아키텍처를 나타냅니다.  

![Architecture](![Image](https://github.com/user-attachments/assets/8c71ba0a-7376-451e-a1a1-37dc674e6c7a))  

---

## 📄 API 문서  

API 명세는 **Postman**에서 확인할 수 있습니다.  
🔗 [API 문서 보기](https://documenter.getpostman.com/view/38853291/2sAYQWLtf7)  

---

## 🗄️ ERD (Entity-Relationship Diagram)  

데이터베이스 모델링을 위한 ERD 다이어그램입니다.  
🔗 [ERD 보기](https://www.erdcloud.com/d/9kNb3ACqSjytwmne3)  

---

## 🚀 배포  

SpotOn은 **AWS 인프라**를 활용하여 배포되었습니다.  

- **EC2**: 백엔드 서버  
- **S3 + CloudFront**: 프론트엔드 정적 파일 배포  
- **RDS (MySQL)**: 데이터베이스  
- **ECR + Docker**: 컨테이너 이미지 관리  
- **GitHub Actions**: CI/CD 자동화  

---
