# ---- Build stage ----
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

# ---- Runtime stage ----
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
