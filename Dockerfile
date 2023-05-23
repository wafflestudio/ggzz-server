FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/ggzz-server.jar"]
CMD ["--spring.profiles.active=dev"]
