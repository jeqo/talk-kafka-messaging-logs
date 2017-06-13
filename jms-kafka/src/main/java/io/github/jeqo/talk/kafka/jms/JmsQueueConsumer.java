package io.github.jeqo.talk.kafka.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

/**
 *
 */
public class JmsQueueConsumer {
    public static void main(String[] args) throws Exception {
        final CamelContext context = new DefaultCamelContext();
        final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "tcp://docker-vm:61616");
        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(
                new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from("jms:queue:queue1")
                                .to("file:/tmp?fileName=jms-${date:now:yyyyMMddHHssSSS}.txt");
                    }
                });
        context.setTracing(true);
        context.start();
    }
}
