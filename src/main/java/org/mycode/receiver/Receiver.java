package org.mycode.receiver;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Component
public class Receiver {
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.queuename.main}")
    private String messageQueueName;
    @Value("${rabbitmq.queuename.response}")
    private String responseQueueName;
    private Map<UUID, Integer> calculatedMessages = new HashMap<>();

    @Autowired
    public Receiver(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void receiveMessage(byte[] message) {
        calculate(new String(message, StandardCharsets.UTF_8));
        if (calculatedMessages.size() == 10) {
            response();
            calculatedMessages.clear();
        }
    }

    private void calculate(String message) {
        Random random = new Random();
        String[] splitMessage = message.split("\\s");
        if (splitMessage.length == 3) {
            try {
                System.out.println(splitMessage[0] + ": " + splitMessage[1] + "+" + splitMessage[2]);
                int result = (random.nextInt(100) >= 10)
                        ? Integer.parseInt(splitMessage[1]) + Integer.parseInt(splitMessage[2])
                        : random.nextInt(500);
                calculatedMessages.put(UUID.fromString(splitMessage[0]), result);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void response() {
        calculatedMessages.keySet().forEach(el -> rabbitTemplate.send(responseQueueName,
                new Message((el.toString() + " " + calculatedMessages.get(el)).getBytes(), new MessageProperties())));
        System.out.println("response calculated messages");
    }
}
