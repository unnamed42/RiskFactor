FROM adoptopenjdk/openjdk11:alpine as build
WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle gradle

RUN ./gradlew --version

COPY build.gradle.bak ./

RUN ./gradlew build --no-daemon --console=rich || return 0

COPY src src

RUN ./gradlew clean bootJar --console=rich

FROM adoptopenjdk/openjdk11:alpine-jre

ARG BUILD=/app/build/libs

COPY --from=build ${BUILD}/*.jar /app/app.jar
