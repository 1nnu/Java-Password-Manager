# Use an official Java runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file to the container
COPY build/libs/passman-0.0.1-SNAPSHOT.jar .

# Expose the port your application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "passman-0.0.1-SNAPSHOT.jar"]

