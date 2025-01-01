FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/person-reg-1.0.0.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]