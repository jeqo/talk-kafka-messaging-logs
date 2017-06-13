FROM jeqo/confluent-platform-kafka:3.2.1

ADD file-sink.properties .
ADD connect-standalone.properties .
RUN mkdir /tmp/output
VOLUME /tmp/output

CMD bin/connect-standalone connect-standalone.properties file-sink.properties
