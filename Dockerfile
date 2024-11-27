# 멀티스테이지 방식으로 빌드 -> 이미지 크기 줄일 수 있음
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY gradlew /app/
COPY gradle /app/gradle
COPY build.gradle /app/
COPY settings.gradle /app/

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY . /app

RUN ./gradlew clean build -x test

FROM openjdk:17-jdk-slim

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]