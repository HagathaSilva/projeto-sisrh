FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -q dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp clean package -DskipTests

FROM tomcat:9.0-jdk17-temurin

ENV TZ=America/Sao_Paulo \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    CATALINA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom" \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -XX:+ExitOnOutOfMemoryError"

RUN rm -rf "$CATALINA_HOME/webapps/*"

COPY --from=build /app/target/sisrh.war "$CATALINA_HOME/webapps/sisrh.war"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=5 \
  CMD curl -fsS http://localhost:8080/sisrh/soap/empregado?wsdl >/dev/null || exit 1

CMD ["catalina.sh", "run"]
