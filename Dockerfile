# Construye y prueba BiCIAM utilizando Maven y Java 17.
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -ntp clean package

# Imagen de ejecucion: Java 17, sin Maven ni archivos fuente.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/biciam-1.0.0.jar /app/biciam.jar
USER 10001:10001
ENTRYPOINT ["java", "-cp", "/app/biciam.jar", "biciam.app.BiCIAMApp"]
CMD ["--demo"]
