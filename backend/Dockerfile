FROM adoptopenjdk/openjdk11:alpine as build
WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle gradle

ENV GRADLE_USER_HOME /cache

RUN ls /cache

VOLUME /cache

RUN ./gradlew --version

COPY build.gradle ./

RUN ./gradlew build --no-daemon --console=rich || return 0

COPY src src

RUN ./gradlew clean bootJar --console=rich

FROM adoptopenjdk/openjdk11:alpine-jre

ARG BUILD=/app/build/libs

COPY --from=build ${BUILD}/*.jar /app/app.jar
