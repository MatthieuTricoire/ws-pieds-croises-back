
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/target/pieds-croises-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT  ["java", "-jar", "app.jar"]
