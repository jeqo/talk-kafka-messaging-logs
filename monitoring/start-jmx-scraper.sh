#!/bin/bash
java -Dcom.sun.management.jmxremote.ssl=false \
     -Djava.rmi.server.hostname=localhost \
     -Dcom.sun.management.jmxremote.authenticate=false \
     -Dcom.sun.management.jmxremote.port=5555 \
     -jar jmx_prometheus_httpserver.jar 8888 kafka.yml