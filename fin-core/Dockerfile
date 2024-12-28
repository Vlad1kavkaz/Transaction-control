FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/fin-core-1.0.0.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]