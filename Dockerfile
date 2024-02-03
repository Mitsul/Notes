FROM openjdk:17
COPY build/libs/notes-0.0.1-SNAPSHOT.jar /app/notes-application.jar
ENTRYPOINT ["java", "-jar", "/app/notes-application.jar"]
