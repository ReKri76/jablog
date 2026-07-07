FROM bellsoft/liberica-openjdk-alpine:21 AS builder

WORKDIR /master_builder
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true

FROM bellsoft/liberica-openjre-alpine:21

WORKDIR /master

COPY --from=builder /master_builder/target/*.war app.war

EXPOSE 8080

CMD ["java", "-jar", "app.war"]