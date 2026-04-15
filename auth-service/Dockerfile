# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .
COPY auth-service/build.gradle auth-service/build.gradle
COPY api-gateway/build.gradle api-gateway/build.gradle
COPY auth-service/src auth-service/src

RUN apk add --no-cache dos2unix && dos2unix gradlew && chmod +x gradlew && ./gradlew :auth-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/auth-service/build/libs/*.jar app.jar
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
