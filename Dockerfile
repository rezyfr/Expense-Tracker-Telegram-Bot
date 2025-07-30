FROM gradle:8.4.0-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle build --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar
CMD ["java", "-jar", "app.jar"]