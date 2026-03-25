FROM eclipse-temurin:17-jdk
ENV TZ=UTC
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 7076
ENTRYPOINT ["java","-jar","app.jar"]