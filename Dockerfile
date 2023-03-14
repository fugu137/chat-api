# Builds the Docker image for the API

FROM eclipse-temurin:17-jdk-alpine as BUILD

COPY gradlew settings.gradle.kts build.gradle.kts /app/

COPY /gradle/ /app/gradle/

COPY /src/ /app/src/

WORKDIR /app/

RUN --mount=type=cache,target=/root/.gradle/ ./gradlew bootJar --info


FROM eclipse-temurin:17.0.6_10-jre as RUN

COPY --from=BUILD /app/build/libs/*.jar /app/chat-api.jar

WORKDIR /app/

ENTRYPOINT ["java", "-jar", "/app/chat-api.jar"]


