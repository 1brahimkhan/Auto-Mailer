# Use a base Java image
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the JAR file from your system into the container
COPY target/*.jar app.jar

# Set the command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]