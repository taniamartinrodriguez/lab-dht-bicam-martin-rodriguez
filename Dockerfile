# Etapa 1: Maven compila y ejecuta la bateria JUnit 5 con Java 17.
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml ./
COPY config ./config
COPY src ./src
RUN mvn -B -ntp -Dstyle.color=never clean package

# Etapa 2: solo Java Runtime y el JAR, sin Maven ni fuentes.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/biciam-1.0.0.jar /app/biciam.jar
USER 10001:10001
ENTRYPOINT ["java", "-cp", "/app/biciam.jar", "biciam.app.BiCIAMApp"]
CMD ["--demo"]
