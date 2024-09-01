# Use a base image with OpenJDK
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the generated JAR file from the Maven build into the container
COPY target/SYMPly_Care-0.0.1-SNAPSHOT.jar /app/symply-care-backend.jar

# Specify the command to run your application
CMD ["java", "-jar", "symply-care-backend.jar"]
