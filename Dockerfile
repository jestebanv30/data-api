# ---------- STAGE 1: build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copiamos lo necesario primero para aprovechar cache
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Descarga dependencias (cache)
RUN ./gradlew dependencies --no-daemon || true

# Ahora copiamos el código
COPY src ./src

# Build del jar
RUN ./gradlew clean bootJar -x test --no-daemon

# ---------- STAGE 2: run ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado
COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
