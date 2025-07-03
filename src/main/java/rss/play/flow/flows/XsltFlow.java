package rss.play.flow.flows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.xml.transformer.MarshallingTransformer;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

@Component
public class XsltFlow {
  @Autowired
  @Qualifier("incoming")
  MessageChannel incomingChannel;

  TransformerFactory transformerFactory = new net.sf.saxon.TransformerFactoryImpl();;

  @Bean
  public MessageChannel xslt() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow xsltFlowConfig() {
    return IntegrationFlow
      .from("xslt")
      .handle(this, "messageHandler")
      .channel("incoming")
      .get();
  }

  public Message<?> messageHandler(Message<?> m) {

    String xsltStylesheet = """
      <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" expand-text="yes">
      
      <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" encoding="utf-8"/>
      <xsl:strip-space elements="*"/>
      
      <xsl:template match="@* | node()">
        <xsl:copy>
          <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
      </xsl:template>
      
      <!-- <xsl:template match="removethis/text()">redacted</xsl:template> -->
      <xsl:template match="report"/>
      <xsl:template match="link"/>
      <xsl:template match="file"/>
      <!--
      <xsl:template match="file">
        <xsl:comment>redacted</xsl:comment>
      </xsl:template>
      -->
      
      </xsl:stylesheet>
    """;

    StringReader xmlReader = new StringReader(m.getPayload().toString());
    StringReader xsltReader = new StringReader(xsltStylesheet);
    StringWriter outputWriter = new StringWriter();

    try {
      Transformer transformer = transformerFactory.newTransformer(new StreamSource(xsltReader));
      transformer.transform(new StreamSource(xmlReader), new StreamResult(outputWriter));
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    String transformed = outputWriter.toString();

    return MessageBuilder
      .withPayload(outputWriter.toString())
      .copyHeaders(m.getHeaders())
      .setHeader("xslt", "transformed")
      .build();
  }
}
