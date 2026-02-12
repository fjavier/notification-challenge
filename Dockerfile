# Multi-stage Dockerfile for notification-challenge
# Stage 1: Build and install lib-notification library
FROM maven:3.9-eclipse-temurin-21-alpine AS lib-builder

WORKDIR /build

# Copy lib-notification project
COPY lib-notification/pom.xml ./lib-notification/
COPY lib-notification/src ./lib-notification/src

# Build and install lib-notification to local Maven repository
WORKDIR /build/lib-notification
RUN mvn clean install -DskipTests

# Stage 2: Build notification-demo
FROM maven:3.9-eclipse-temurin-21-alpine AS demo-builder

WORKDIR /build

# Copy the installed lib-notification from previous stage
COPY --from=lib-builder /root/.m2/repository/com/example/lib-notification /root/.m2/repository/com/example/lib-notification

# Copy notification-demo project
COPY notification-demo/pom.xml ./notification-demo/
COPY notification-demo/src ./notification-demo/src

# Build notification-demo (this will use lib-notification from local Maven repo)
WORKDIR /build/notification-demo
RUN mvn clean package -DskipTests

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from demo-builder stage
COPY --from=demo-builder /build/notification-demo/target/notification-demo-1.0-SNAPSHOT.jar /app/notification-demo.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/notification-demo.jar"]
