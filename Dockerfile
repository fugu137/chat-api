FROM eclipse-temurin:17.0.6_10-jre

COPY ./build/libs/ /app

WORKDIR /app

ENTRYPOINT java -jar $(ls)