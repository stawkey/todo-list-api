# Use Eclipse Temurin JDK 21 for build stage
FROM eclipse-temurin:21 AS builder

# Set the working directory
WORKDIR /app

# Copy project files into the container
COPY . .

# Run the Maven build (skip tests if desired)
RUN ./mvnw clean package -DskipTests

# Use a smaller base image with JDK 21 for running the app
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the built jar file from the builder stage
COPY --from=builder /app/target/*.jar todo-app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "todo-app.jar"]