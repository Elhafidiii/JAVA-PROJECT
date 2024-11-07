FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/Manic_time-1.0-SNAPSHOT.jar javaproject.jar
EXPOSE 8000
ENTRYPOINT exec java $JAVA_OPTS -jar javaproject.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar javaproject.jar
