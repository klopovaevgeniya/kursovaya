# Самый базовый и проверенный образ
FROM maven:3.8.5-openjdk-17

WORKDIR /app
COPY . .

# Собираем проект
RUN mvn clean package -DskipTests

# Запускаем
EXPOSE 8080
CMD ["java", "-jar", "target/*.jar"]