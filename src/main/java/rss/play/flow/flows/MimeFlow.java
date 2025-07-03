package rss.play.flow.flows;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class MimeFlow {
  @Bean
  public MessageChannel mime() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow mimeFlowConfig() {
    return IntegrationFlow
      .from("mime")
      .handle(this, "messageHandler")
      //.channel("incoming")
      .channel("xslt")
      .get();
  }

  public Message<?> messageHandler(Message<?> message) throws MessagingException, IOException {
    DataSource dataSource = new ByteArrayDataSource(message.getPayload().toString().getBytes(), MediaType.MULTIPART_FORM_DATA.toString());
    Multipart multipart1 = new MimeMultipart(dataSource);

    Object header = null;
    Object body = null;

    int c = multipart1.getCount();
    for (int i = 0; i < multipart1.getCount(); i++) {
      MimeBodyPart bp = (MimeBodyPart) multipart1.getBodyPart(i);
      System.out.println(bp.getContentType());
      Object value = bp.getContent();
      if (value instanceof String vs) {
        header = vs;

      } else if (value instanceof InputStream is) {
        body = new String(is.readAllBytes());
      }
    }
    return MessageBuilder
      .withPayload(body)
      .copyHeaders(message.getHeaders())
      .setHeader("received", header)
      .build();
  }
}
