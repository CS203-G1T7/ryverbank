FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8080
COPY data/keystore.p12 data/
ENTRYPOINT ["java","-jar","/application.jar"]