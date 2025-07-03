package rss.play.flow.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "flow.xslt", havingValue = "true", matchIfMissing = true)
public class XmlSource {
  @Autowired
  @Qualifier("xslt")
  MessageChannel xsltChannel;
  MessagingTemplate template = new MessagingTemplate();


  //@Scheduled(fixedRate = 5000)
  public void onTimeout() {
    String xmlInput = """
    <root>
      <data>Some data</data>
      <a>
        <removethis>This element will be removed</removethis>
      </a>
      <otherdata>More data</otherdata>
    </root>
    """;

    Message<?> message = MessageBuilder.withPayload(xmlInput).build();
    Message<?> response = template.sendAndReceive(xsltChannel, message);
    System.out.println(response);
  }
}
