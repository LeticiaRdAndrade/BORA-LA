# 1) Compila o React (gera a pasta dist)
FROM node:22-alpine AS front
WORKDIR /front
COPY frontend/package*.json ./
RUN npm ci
COPY frontend .
RUN npm run build

# 2) Compila o Java, colocando o site dentro da pasta static
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
RUN apk add --no-cache bash
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
COPY --from=front /front/dist ./src/main/resources/static
RUN ./mvnw package -DskipTests -B

# 3) Imagem final, só com o necessário para rodar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# o nome do .jar vem do artifactId + version do pom.xml
COPY --from=build /app/target/roteiro-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
