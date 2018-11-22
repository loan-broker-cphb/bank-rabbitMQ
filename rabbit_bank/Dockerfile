FROM java:8
VOLUME /tmp
ADD target/rabbit-bank-0.0.1-SNAPSHOT.jar app.jar
ADD src/main/resources resources
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java" ,"-jar", "/app.jar"]
