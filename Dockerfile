FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# 의존성 캐싱
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

# 소스 복사 및 빌드
COPY src ./src
RUN gradle bootJar --no-daemon
# clean 제거 - Docker에서는 이미 깨끗한 환경

FROM eclipse-temurin:21-jre
WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]