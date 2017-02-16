FROM jeqo/confluent-platform-client:3.1.1-2.11

ADD file-sink.properties .
ADD connect-standalone.properties .
RUN mkdir /output
VOLUME /output

CMD bin/connect-standalone connect-standalone.properties file-sink.properties
