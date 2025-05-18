FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies separately to leverage Docker layer caching
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar
# Expose the application port
EXPOSE 8080
# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]