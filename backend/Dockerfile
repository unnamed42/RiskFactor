FROM adoptopenjdk/openjdk11:alpine as build
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle

COPY src src

RUN ./gradlew bootJar --no-daemon

FROM adoptopenjdk/openjdk11:alpine-jre
ARG BUILD=/app/build/libs
ARG PKGNAME=backend

COPY --from=build ${BUILD}/${PKGNAME}.jar /app.jar
