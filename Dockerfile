# Use a base image with OpenJDK
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY SYMPly_Care-0.0.1-SNAPSHOT.jar  /app/SYMPly_Care-0.0.1-SNAPSHOT.jar 

# Specify the command to run your application
CMD ["java", "-jar", "SYMPly_Care-0.0.1-SNAPSHOT.jar"]
