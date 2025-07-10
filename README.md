# Swibee

Spring Boot 기반 웹 애플리케이션

## 🚀 실행 방법

### 1. 개발 환경 실행 (권장)

**IntelliJ에서 개발할 때**

```bash
# 1. 데이터베이스 서비스만 실행
docker-compose up -d

# 2. IntelliJ에서 SwibeeApplication 실행
# 또는 터미널에서
./gradlew bootRun
```

- Spring Boot 애플리케이션: 로컬에서 실행 (8080 포트)
- 데이터베이스들: Docker에서 실행 (PostgreSQL, MongoDB, Redis)
- 핫 리로드, 디버깅 등 개발 기능 모두 사용 가능

### 2. 전체 Docker 환경 실행

**운영 환경이나 전체 스택 테스트**

```bash
# 전체 애플리케이션 스택 실행
docker-compose -f docker-compose.prod.yml up -d

# 로그 확인
docker-compose -f docker-compose.prod.yml logs -f app

# 종료
docker-compose -f docker-compose.prod.yml down
```

## 📁 파일 구조

```
├── compose.yaml              # 개발용 - 데이터베이스만
├── docker-compose.prod.yml   # 배포용 - 전체 스택
├── Dockerfile               # Spring Boot 애플리케이션 이미지
├── src/main/resources/
│   ├── application.yml      # 기본 설정
│   ├── application-dev.yml  # 개발 환경 설정
│   ├── application-prod.yml # 운영 환경 설정
│   └── application-docker.yml # Docker 환경 설정
```

## 🔧 환경별 설정

| 환경 | 프로파일 | 설명 |
|------|----------|------|
| 개발 | `dev` | 로컬 개발 (기본값) |
| Docker | `docker` | Docker 컨테이너 환경 |
| 운영 | `prod` | 운영 환경 |

## 📊 서비스 포트

- **Spring Boot**: 8080
- **PostgreSQL**: 5432
- **MongoDB**: 27017
- **Redis**: 6379

## 🛠️ 개발 팁

### 데이터베이스 초기화
```bash
# 개발 환경 DB 초기화
docker-compose down -v
docker-compose up -d
```

### 애플리케이션 재빌드
```bash
# Docker 이미지 재빌드
docker-compose -f docker-compose.prod.yml build app
docker-compose -f docker-compose.prod.yml up -d app
```

## 🔍 문제 해결

### 포트 충돌
- 개발 시에는 `compose.yaml`만 사용 (DB만 실행)
- 배포 시에는 `docker-compose.prod.yml` 사용 (전체 스택)

### 연결 오류
- 개발 환경: `localhost:5432` (PostgreSQL)
- Docker 환경: `postgres:5432` (서비스 이름 사용) 