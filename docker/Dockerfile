FROM eclipse-temurin:21-jre
# Change to this if you need a China mirror for faster pulls
# FROM m.daocloud.io/docker.io/library/eclipse-temurin:21-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file into the container
# Please modify according to your actual JAR file name
#local
#COPY build/libs/CoolLeaf-0.0.1-SNAPSHOT.jar app.jar

#server
COPY CoolLeaf-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port 8080
EXPOSE 8080

# Startup command
ENTRYPOINT ["java", "-jar", "app.jar"]