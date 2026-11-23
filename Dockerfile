FROM maven:3-eclipse-temurin-21 AS build

ARG MODULE=msvc-auth

WORKDIR /app

COPY . .

RUN mvn -q -pl ${MODULE} -am -DskipTests package

FROM eclipse-temurin:21-jre

ARG MODULE=msvc-auth

WORKDIR /app

COPY --from=build /app/${MODULE}/target/${MODULE}-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70"

ENTRYPOINT ["java", "-jar", "app.jar"]
