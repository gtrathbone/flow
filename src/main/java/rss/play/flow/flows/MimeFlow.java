package rss.play.flow.flows;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@ConditionalOnProperty(value = "flow.mime", havingValue = "true", matchIfMissing = true)
public class MimeFlow {

  @Scheduled(fixedRate = 1000)
  public void creatMessage() throws MessagingException, IOException {

    MimeMultipart mp = new MimeMultipart();

    MimeBodyPart bp1 = new MimeBodyPart();
    bp1.setContent("hello", "application/text");
    bp1.setContentID("id-1");
    mp.addBodyPart(bp1);


    MimeBodyPart bp2 = new MimeBodyPart();
    FileDataSource fds = new FileDataSource("/Users/george/Documents/IMG_1191.png");
    bp2.setDataHandler(new DataHandler(fds));
    bp2.addHeader("Content-Type", "application/octet-stream");
    bp2.addHeaderLine("Content-Transfer-Encoding: base64");
    mp.addBodyPart(bp2);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    mp.writeTo(os);
    String rep = os.toString();
    System.out.println(rep);

    Multipart multipart1 = new MimeMultipart(new ByteArrayDataSource(rep.getBytes(), MediaType.MULTIPART_FORM_DATA.toString()));
    int c = multipart1.getCount();
    System.out.println(multipart1.getCount());
    for (int i = 0; i < multipart1.getCount(); i++) {
      MimeBodyPart bp = (MimeBodyPart) multipart1.getBodyPart(i);
      System.out.println(bp.getContentType());
      Object value = bp.getContent();
      if (value instanceof String) {
        System.out.println(value);
      }
      else if (value instanceof InputStream) {
        System.out.println("InputStream");
      }
    }
  }
}
