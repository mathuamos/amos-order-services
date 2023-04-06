FROM openjdk:11
EXPOSE 8080
ARG JAR_FILE=target/jagaad-order-services-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} order-service.jar
ENV TZ="Africa/Nairobi"
RUN date
ENTRYPOINT ["java","-jar","/jagaad-order-service.jar"]