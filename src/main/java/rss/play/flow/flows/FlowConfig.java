package rss.play.flow.flows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class FlowConfig {
  @Autowired
  ApplicationContext context;

  @Bean("incoming")
  public MessageChannel incoming() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow integrationFlow() {
    return IntegrationFlow.from("incoming")
      .handle(this, "messageHandler")
      .gateway("transforming")
      .gateway("printing")
      //.channel("error-generator")
      //.handle(System.out::println)
      .get();
  }

  //@ServiceActivator
  public Message<?> messageHandler(Message<?> m) {
    return MessageBuilder
      .withPayload(m.getPayload())
      .copyHeaders(m.getHeaders())
      .setHeader("two", "two two")
      .build();
  }

  @Bean
  public IntegrationFlow transformingFlow() {
    return IntegrationFlow.from("transforming")
      .transform(Message.class, m -> MessageBuilder.withPayload(m.getPayload() + " transformed").copyHeaders(m.getHeaders()).build())
      //.handle(System.out::println)
      .get();
  }
  @Bean
  public IntegrationFlow printingFlow() {
    return IntegrationFlow.from("printing")
      .transform(Message.class, m -> MessageBuilder.withPayload(m.getPayload() + " printing").copyHeaders(m.getHeaders()).build())
      //.log(LoggingHandler.Level.INFO, null,m -> m.getHeaders().getId() + ": " + m.getPayload())
      .log()
      //.handle(System.out::println)
      .get();
  }

  @Bean
  public IntegrationFlow errorGeneratorFlow() {
    return IntegrationFlow.from("error-generator")
      .transform(Message.class, m -> MessageBuilder.withPayload(m.getPayload() + " error").copyHeaders(m.getHeaders()).build())
      .handle(System.out::println)
      .get();
  }
}
