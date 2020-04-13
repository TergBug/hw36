package org.mycode;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@EnableRabbit
public class CommonConfig {
    @Value("${rabbitmq.queuename.main}")
    protected String messageQueueName;
    @Value("${rabbitmq.queuename.response}")
    protected String responseQueueName;
    @Value("${rabbitmq.queuename.error}")
    protected String errorQueueName;

    @Bean
    public Queue mainQueue() {
        return new Queue(messageQueueName, false);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(responseQueueName, false);
    }

    @Bean
    public Queue errorQueue() {
        return new Queue(errorQueueName, false);
    }
}
