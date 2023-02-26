# Builds the Docker image for the API

FROM eclipse-temurin:17-jdk-alpine as BUILD

COPY build.gradle.kts settings.gradle.kts gradlew /app/

COPY /gradle/ /app/gradle/

WORKDIR /app/

RUN ./gradlew build --info || true

COPY /src/ /app/src/

RUN ./gradlew bootJar --info


FROM eclipse-temurin:17.0.6_10-jre as RUN

COPY --from=BUILD /app/build/libs/*.jar /app/chat-api.jar

WORKDIR /app/

ENTRYPOINT ["java", "-jar", "/app/chat-api.jar"]


