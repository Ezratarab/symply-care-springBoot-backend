# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /

# Copy the local code to the container's workspace
COPY . .

# Use Maven to build the application (if applicable)
# RUN mvn clean package -DskipTests

# If you already have the JAR file, copy it directly
# COPY target/your-application.jar /app/your-application.jar

# Run the application
ENTRYPOINT ["java", "-jar", "target/your-application.jar"]

# Expose port (adjust based on your application)
EXPOSE 8080
