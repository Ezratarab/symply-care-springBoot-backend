# Use a lightweight OpenJDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the Maven build stage
COPY SYMPly_Care-0.0.1-SNAPSHOT.jar /app/symply-care-backend.jar

# Expose the port your app listens on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "symply-care-backend.jar"]