FROM openjdk:8-jdk

ENV JAR_FILE kafka-producer-jar-with-dependencies.jar

ADD target/${JAR_FILE} .

CMD java -jar ${JAR_FILE}