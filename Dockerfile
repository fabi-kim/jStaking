 # Build stage
FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /app

# Gradle wrapper와 설정 파일만 먼저 복사 (캐시 최적화)
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle/

# 의존성 다운로드 (캐시됨)
RUN ./gradlew dependencies --no-daemon

# 소스코드 복사 및 빌드
COPY src src/
RUN ./gradlew clean build -x test --no-daemon

# Runtime stage
FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

# 비루트 사용자 생성 (보안)
RUN addgroup -g 1000 appgroup && \
adduser -D -u 1000 -G appgroup appuser && \
chown -R appuser:appgroup /app

# JAR 파일 복사
COPY --from=builder --chown=appuser:appgroup /app/build/libs/*.jar app.jar

# 비루트 사용자로 전환
USER appuser

# 환경변수 (런타임에 오버라이드 가능)
ENV SPRING_PROFILES_ACTIVE=local
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

# 헬스체크 (actuator 필요)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1


# JVM 옵션과 함께 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
