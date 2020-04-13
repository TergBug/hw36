package org.mycode.sender;

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
public class Sender {
    @Value("${rabbitmq.queuename.main}")
    private String messageQueueName;
    @Value("${rabbitmq.queuename.response}")
    private String responseQueueName;
    @Value("${rabbitmq.queuename.error}")
    private String errorQueueName;
    private Map<UUID, String> messages = new HashMap<>();
    private Map<UUID, Integer> responses = new HashMap<>();
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public Sender(RabbitTemplate rabbitTemplate) {
        Random random = new Random();
        this.rabbitTemplate = rabbitTemplate;
        for (int i = 0; i < 10; i++) {
            UUID uuid = UUID.randomUUID();
            int a = (random.nextInt(200) - 100);
            int b = (random.nextInt(200) - 100);
            messages.put(uuid, a + " " + b);
            responses.put(uuid, a + b);
        }

    }

    public void sendMessage() {
        messages.keySet().forEach(el -> rabbitTemplate.send(messageQueueName,
                new Message((el.toString() + " " + messages.get(el)).getBytes(), new MessageProperties())));
        System.out.println("send messages");
    }

    public void receiveResponse(byte[] message) {
        checkResponses(new String(message, StandardCharsets.UTF_8));
    }

    private void checkResponses(String message) {
        String[] splitMessage = message.split("\\s");
        if (splitMessage.length == 2) {
            try {
                Integer rightAnswer = responses.get(UUID.fromString(splitMessage[0]));
                if (rightAnswer != null && Integer.parseInt(splitMessage[1]) == rightAnswer) {
                    System.out.println(splitMessage[0] + ": " + splitMessage[1] + " - OK");
                } else {
                    rabbitTemplate.send(errorQueueName,
                            new Message((splitMessage[0] + ": " + splitMessage[1] + " - wrong answer").getBytes(),
                                    new MessageProperties()));
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
