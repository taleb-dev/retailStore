# Multi-stage Dockerfile for Spring Boot (Java 21)

# 1) Build stage
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy Maven wrapper/settings and pom for dependency caching
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw

# Pre-fetch dependencies (uses only pom.xml for better caching)
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Copy source and build
COPY src src
RUN ./mvnw -q -DskipTests package

# 2) Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy fat jar from build stage
COPY --from=build /workspace/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

# Allow env overrides for jwt and mongodb via Spring's relaxed binding
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
