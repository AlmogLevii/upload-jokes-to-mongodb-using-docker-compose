FROM registry.access.redhat.com/ubi8/openjdk-11

WORKDIR /usr/app

COPY ./target/containers-first-task-1.0-SNAPSHOT-jar-with-dependencies.jar .

CMD ["java","-jar","containers-first-task-1.0-SNAPSHOT-jar-with-dependencies.jar"]