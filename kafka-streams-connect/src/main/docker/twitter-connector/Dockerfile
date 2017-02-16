FROM jeqo/confluent-platform-client:3.1.1-2.11

ADD kafka-connect-twitter-0.1-jar-with-dependencies.jar .
ADD connect-source-standalone.properties .
ADD twitter-source.properties .

ENV CLASSPATH=kafka-connect-twitter-0.1-jar-with-dependencies.jar

CMD bin/connect-standalone connect-source-standalone.properties twitter-source.properties