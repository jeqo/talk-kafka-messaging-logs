package io.github.jeqo.talk.kafka.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;

/**
 *
 */
public class JmsTopicConsumer1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsTopicConsumer1.class);

    public static void main(String[] args) throws Exception {
        LOGGER.info("About to run topic-consumer1 integration...");


        final CamelContext context = new DefaultCamelContext();
        final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "tcp://docker-vm:61616");
        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(
                new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        log.info("About to start route: JMS Topic -> Log ");
                        from("jms:topic:topic1")
                                .log("${body}");
                    }
                });
        context.setTracing(true);
        context.start();

        // let it run for 5 minutes before shutting down
        Thread.sleep(5 * 60 * 1000);

        context.stop();
    }
}
