# -------------------------------------------------
# 1) Build stage: compilar tu app con Maven
# -------------------------------------------------
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos solo pom.xml primero para cachear dependencias
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos sin tests para acelerar builds
COPY src ./src
RUN mvn clean package -DskipTests -B

# -------------------------------------------------
# 2) Runtime stage: empaquetar el JAR y correrlo
# -------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

# Directorio de trabajo
WORKDIR /app

# Copiamos el JAR compilado desde el builder
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto (Render inyecta PORT en runtime)
EXPOSE 8080

# ENTRYPOINT que usa la variable $PORT de Render si está definida,
# o 8080 como fallback.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
