FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
COPY config ./config
RUN mvn --batch-mode verify

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S vertex && adduser -S vertex -G vertex
WORKDIR /app
COPY --from=build /workspace/target/vertex-sdd-hub-0.1.0-SNAPSHOT.jar app.jar
USER vertex
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
