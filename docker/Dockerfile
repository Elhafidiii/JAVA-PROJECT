# Use an OpenJDK base image
FROM openjdk:20-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built application JAR file into the container
COPY target/*.jar app.jar

# Expose the default JavaFX application display port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]