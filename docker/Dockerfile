FROM adoptopenjdk/openjdk11:alpine as build
WORKDIR /app

COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -Dmaven.test.skip=true

FROM adoptopenjdk/openjdk11:alpine-jre
ARG BUILD=/app/target
ARG PKGNAME=riskfactor-0.0.1-SNAPSHOT

COPY --from=build ${BUILD}/${PKGNAME}.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

