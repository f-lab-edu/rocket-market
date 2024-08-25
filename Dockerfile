FROM bellsoft/liberica-openjdk-alpine:17
CMD ["./gradlew", "clean", "build"]
ARG JAR_FILE=build/libs/rocket-market-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]