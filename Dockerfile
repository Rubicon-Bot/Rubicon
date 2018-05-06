FROM openjdk:8

COPY /target/rubicon-bot.jar rubicon-bot.jar
ENTRYPOINT ["java", "-jar", "rubicon-bot.jar"]