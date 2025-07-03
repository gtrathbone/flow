package rss.play.flow.source;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@ConditionalOnProperty(value = "flow.mime", havingValue = "true", matchIfMissing = true)
public class MimeSource {
  @Autowired
  @Qualifier("mime")
  MessageChannel mimeChannel;
  MessagingTemplate template = new MessagingTemplate();

  @Scheduled(fixedRate = 5000)
  public void onTimeout() throws MessagingException, IOException {
    MimeMultipart mp = new MimeMultipart();

    MimeBodyPart bp1 = new MimeBodyPart();
    bp1.setContent("hello", "application/text");
    bp1.setContentID("id-1");
    mp.addBodyPart(bp1);

    MimeBodyPart bp2 = new MimeBodyPart();
    FileDataSource fds = new FileDataSource("/Users/george/Documents/directory_list.xml");
    bp2.setDataHandler(new DataHandler(fds));
    bp2.addHeader("Content-Type", "application/octet-stream");
    bp2.addHeaderLine("Content-Transfer-Encoding: base64");
    mp.addBodyPart(bp2);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    mp.writeTo(os);
    String rep = os.toString();

    Message<?> message = MessageBuilder.withPayload(rep).build();
    Message<?> response = template.sendAndReceive(mimeChannel, message);
    System.out.println(response);

  }
}
