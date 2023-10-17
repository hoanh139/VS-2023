FROM maven:latest as builder
WORKDIR /app
COPY pom.xml /app
COPY src /app/src
RUN sed -i 's/to substitute/borse.Main/' pom.xml
RUN mvn assembly:assembly -DdescriptorId=jar-with-dependencies

FROM openjdk:18-slim
WORKDIR /app
COPY --from=builder app/target/app-1.0-jar-with-dependencies.jar /app/app.jar
