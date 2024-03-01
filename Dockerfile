FROM openjdk:17
COPY target/smart_garden_server-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 4567
ENTRYPOINT ["java","-jar","/app.jar"]