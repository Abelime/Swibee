# 빌드 스테이지
FROM gradle:8.7-jdk21-alpine AS builder

WORKDIR /app

# Gradle 캐시 최적화를 위해 build.gradle과 settings.gradle 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/

# 의존성 다운로드
RUN gradle dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src/ src/
RUN gradle build --no-daemon -x test

# 런타임 스테이지
FROM openjdk:21-jdk-slim

WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"] 