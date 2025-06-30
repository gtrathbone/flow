package rss.play.flow.flows;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class MimeFlow {

  @Scheduled(fixedRate = 1000)
  public void creatMessage() throws MessagingException, IOException {

    //Session session = Session.getDefaultInstance(System.getProperties());
    //MimeMessage mm = new MimeMessage(session);
    MimeMultipart mp = new MimeMultipart();
    String ct = mp.getContentType();

    MimeBodyPart bp1 = new MimeBodyPart();
    bp1.setContent("hello", "application/text");
    bp1.setContentID("id-1");
    mp.addBodyPart(bp1);

    File f = new File("/Users/george/Documents/IMG_1191.png");
    f.exists();
    FileInputStream fis = new FileInputStream("/Users/george/Documents/IMG_1191.png");

    FileDataSource fds = new FileDataSource("/Users/george/Documents/IMG_1191.png");

    Multipart multipart = new MimeMultipart();
    MimeBodyPart bp2 = new MimeBodyPart();
    bp2.setDataHandler(new DataHandler(fds));
    //bp2.setContent(fis.readAllBytes(), "application/octet-stream");
    bp2.addHeader("Content-Type", "application/octet-stream");
    bp2.addHeaderLine("Content-Transfer-Encoding: base64");
    mp.addBodyPart(bp2);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    mp.writeTo(os);
    String rep = os.toString();
    System.out.println(rep);
  }
}
