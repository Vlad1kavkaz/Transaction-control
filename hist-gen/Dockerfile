FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/hist-gen-1.0.0.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]