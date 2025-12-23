# ===== BUILD STAGE =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom first (better caching)
COPY services/backend/pom.xml .
RUN mvn dependency:go-offline

# Copy backend source
COPY services/backend .
RUN mvn clean package -DskipTests

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "app.jar"]
