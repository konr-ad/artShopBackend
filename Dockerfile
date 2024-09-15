# Use Amazon Corretto 22 as the base image
FROM amazoncorretto:22-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the pre-built JAR file from the local machine to the container
COPY target/artShop-1.0.0.jar /app/artShop-1.0.0.jar

# Expose the port that your Spring Boot app will run on
EXPOSE 8080

# Define the entrypoint to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/artShop-1.0.0.jar"]
