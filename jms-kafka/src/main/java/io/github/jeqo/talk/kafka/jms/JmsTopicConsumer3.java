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
public class JmsTopicConsumer3 {
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
                        from("jms:topic:topic1" +
                                "?clientId=client3" +
                                "&durableSubscriptionName=client3" +
                                "&acknowledgementModeName=AUTO_ACKNOWLEDGE")
                                .to("log:jms-topic-consumer2?showAll=true")
                                .to("file:/tmp?fileName=jms-topic1-client2-${date:now:yyyyMMddHHssSSS}.txt");
                    }
                });
        context.setTracing(true);
        context.start();
    }
}
