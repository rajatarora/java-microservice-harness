FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# Copy Gradle wrapper and project files
# Use the Gradle wrapper to ensure reproducible builds without installing Gradle in the image
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle ./
COPY src src

# Make wrapper executable and build the fat JAR
RUN chmod +x ./gradlew && ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the built jar produced by Spring Boot plugin (bootJar) from the builder stage
COPY --from=builder /workspace/build/libs/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
