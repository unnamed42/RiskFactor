FROM adoptopenjdk/openjdk11:alpine as build
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle

COPY src src

RUN mkdir -p /root/.gradle
VOLUME /root/.gradle

RUN ./gradlew bootJar --no-daemon --console rich

FROM adoptopenjdk/openjdk11:alpine-jre
ARG BUILD=/app/build/libs

COPY --from=build ${BUILD}/*.jar /app.jar
