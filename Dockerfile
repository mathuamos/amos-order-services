FROM openjdk:11
EXPOSE 9090
ARG JAR_FILE=target/cart-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} cart-service.jar
ENV TZ="Africa/Nairobi"
RUN date
ENTRYPOINT ["java","-jar","/cart-service.jar"]