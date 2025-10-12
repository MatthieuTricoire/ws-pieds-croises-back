# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copie des fichiers maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Télécharger les dépendances
RUN ./mvnw dependency:go-offline

# Copier le code source et builder
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run (JRE au lieu de JDK)
FROM eclipse-temurin:21-jre-jammy

# Argument pour définir l'environnement (par défaut: prod)
ARG SPRING_PROFILE=prod

WORKDIR /app

# Copier le JAR depuis le builder
COPY --from=builder /app/target/pieds-croises-0.0.1-SNAPSHOT.jar app.jar

# Définir le profil Spring via variable d'environnement
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}

# Exposer le port Spring Boot
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
