# ОБРАЗ С JAVA 21 - ТОЧНО ПОДХОДИТ ДЛЯ ТВОЕГО POM.XML
FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app
COPY . .

# Собираем проект
RUN mvn clean package -DskipTests

# Запускаем
EXPOSE 8080
CMD ["java", "-jar", "target/AutoDetail-0.0.1-SNAPSHOT.jar"]