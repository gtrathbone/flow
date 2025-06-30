package rss.play.flow.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.jms.ConnectionFactoryUnwrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "source.activemq", havingValue = "true", matchIfMissing = true)
public class ActiveMQInterface {

  @Autowired
  @Qualifier("incoming")
  MessageChannel incomingChannel;
  MessagingTemplate template = new MessagingTemplate();

  @Autowired
  JmsTemplate jmsTemplate;

  @Autowired
  ApplicationContext context;

  @Bean
  public DefaultJmsListenerContainerFactory myFactory(DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, ConnectionFactoryUnwrapper.unwrapCaching(connectionFactory));
    //factory.setMessageConverter(new MyMessageConverter());
    return factory;
  }

  @JmsListener(destination = "spring-boot")
  public void receiveMessage(String message) {
    Message<?> m = MessageBuilder
      .withPayload(message)
      .setHeader("one", "one").build();
    Message<?> r = template.sendAndReceive(incomingChannel, m);
    System.out.println(r);
//    context.getBean("incoming", MessageChannel.class)
//      .send(MessageBuilder.withPayload(message).setHeader("one", "one").build());
  }

  @Scheduled(fixedRate = 5000)
  public void onSchedule() {
    jmsTemplate.convertAndSend("spring-boot", "hello from ActiveMQ");
  }

}
