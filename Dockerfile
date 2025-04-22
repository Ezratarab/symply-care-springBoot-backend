FROM openjdk:17-jdk-slim
WORKDIR /app
COPY SYMPly_Care-0.0.1-SNAPSHOT.jar /app/symply-care-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "symply-care-backend.jar"]
