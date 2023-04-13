FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . /app
RUN ./gradlew :api:bootJar
EXPOSE 8080
CMD java -jar api/build/libs/nostalgia-api.jar
