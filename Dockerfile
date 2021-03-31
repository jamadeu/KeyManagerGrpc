FROM openjdk:11
COPY build/libs/*all.jar app.jar
EXPOSE 50051
ENTRYPOINT ["java", "-jar", "/app.jar"]