FROM bellsoft/liberica-openjdk-alpine:21 AS builder

WORKDIR /master_builder
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package

FROM bellsoft/liberica-openjre-alpine:21

WORKDIR /master

RUN addgroup -S dev && adduser -S main -G dev

RUN mkdir -p /master/logs

COPY --from=builder /master_builder/target/*.jar app.jar

RUN chown -R main:dev /master

USER main

EXPOSE 8080

CMD ["java", "-jar", "app.war"]