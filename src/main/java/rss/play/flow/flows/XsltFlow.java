package rss.play.flow.flows;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@ConditionalOnProperty(value = "flow.xml", havingValue = "true", matchIfMissing = true)
public class XsltFlow {

  @Bean
  public TransformerFactory transformerFactory() {
    return new net.sf.saxon.TransformerFactoryImpl();
  }

  @Scheduled(fixedRate = 5000)
  public void transform() throws TransformerException {
    TransformerFactory transformerFactory = transformerFactory();

    String xmlInput = """
    <root>
      <data>Some data</data>
      <a>
        <removethis>This element will be removed</removethis>
      </a>
      <otherdata>More data</otherdata>
    </root>
    """;

    String xsltStylesheet = """
      <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      
      <xsl:output omit-xml-declaration="yes" indent="yes"/>
      <xsl:strip-space elements="*"/>
      
      <xsl:template match="@* | node()">
        <xsl:copy>
          <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
      </xsl:template>
      
      <xsl:template match="removethis/text()"/>
      
      </xsl:stylesheet>
    """;

    StringReader xmlReader = new StringReader(xmlInput);
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
    System.out.println(transformed);
  }
}
