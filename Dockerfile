FROM maven:3.8.6-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
# Замени AutoDetail-0.0.1-SNAPSHOT.jar на актуальное имя из target/
COPY --from=builder /app/target/AutoDetail-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]