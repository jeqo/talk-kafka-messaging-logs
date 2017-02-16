FROM jeqo/apache-tomee:plus-7.0.2

ENV CATALINA_OPTS "-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
ENV JAVA_OPTS "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

ADD target/kafka-streams-connect.war $TOMEE_HOME/webapps/

EXPOSE 8000
EXPOSE 9999