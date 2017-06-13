package io.github.jeqo.talk.kafka.kafka;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.impl.DefaultCamelContext;

/**
 *
 */
public class KafkaOtherConsumer {

    public static void main(String[] args) throws Exception {
        try {
            final CamelContext context = new DefaultCamelContext();
            context.addRoutes(
                    new RouteBuilder() {
                        @Override
                        public void configure() throws Exception {
                            from("kafka:more-partitions" +
                                    "?brokers=docker-vm:9092" +
                                    "&groupId=group1" +
                                    "&autoOffsetReset=earliest")
                                    .process(exchange -> {
                                        String messageKey = "";
                                        if (exchange.getIn() != null) {
                                            final Message message = exchange.getIn();
                                            final Integer partitionId = (Integer) message
                                                    .getHeader(KafkaConstants.PARTITION);
                                            final String topicName = (String) message
                                                    .getHeader(KafkaConstants.TOPIC);
                                            if (message.getHeader(KafkaConstants.KEY) != null)
                                                messageKey = (String) message
                                                        .getHeader(KafkaConstants.KEY);
                                            Object data = message.getBody();

                                            System.out.println("topicName :: "
                                                    + topicName + " partitionId :: "
                                                    + partitionId + " messageKey :: "
                                                    + messageKey + " message :: "
                                                    + data + "\n");
                                        }
                                    }).to("log:input?level=TRACE");
                        }
                    });
            context.setTracing(true);
            context.start();

            // let it run for 5 minutes before shutting down
            Thread.sleep(5 * 60 * 1000);

            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
