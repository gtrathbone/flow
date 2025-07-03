package rss.play.flow.source;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "source.rabbit", havingValue = "true", matchIfMissing = false)
public class RabbitMQInterface {
  @Autowired
  @Lazy
  RabbitMQInterface proxy;

  @Autowired
  @Qualifier("incoming")
  MessageChannel incomingChannel;
  MessagingTemplate template = new MessagingTemplate();

  @Autowired
  ApplicationContext context;
  @Autowired
  RabbitTemplate rabbitTemplate;

  static final String topicExchangeName = "spring-boot-exchange";
  static final String queueName = "spring-boot";


  @Bean
  Queue queue() {
    return new Queue(queueName, false);
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(topicExchangeName);
  }

  @Bean
  Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with("*");
  }

  @RabbitListener(queues = queueName)
  public void onMessage(Message message) {
    Message<?> m = MessageBuilder
      .withPayload(message.getPayload())
      .setHeader("one", "one").build();
    Message<?> r = template.sendAndReceive(incomingChannel, m);
    System.out.println(r);
  }


  @Scheduled(fixedRate = 5000)
  public void onSchedule() {
    rabbitTemplate.convertAndSend(topicExchangeName, "hello", "hello from RabbitMQ");
  }

}
