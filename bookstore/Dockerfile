FROM openjdk:8-jre

ARG jarToCopy

COPY /target/$jarToCopy /app/app.jar

CMD [ "java", "-cp", "app/app.jar", "mancio.bookstore.app.Main" ]