# ================================
# BUILD STAGE
# ================================
FROM gradle:8-jdk21 AS build

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle* .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY src/ src/

RUN ./gradlew clean bootJar -x test --no-daemon


# ================================
# RUNTIME STAGE
# ================================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-XX:+UseContainerSupport","-jar","app.jar"]
