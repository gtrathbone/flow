package rss.play.flow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestController {
  @Autowired
  ApplicationContext context;

  @GetMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public void ingest() {
    context.getBean("incoming", MessageChannel.class)
      .send(
        MessageBuilder
          .withPayload("from HTTP")
          .setHeader("one", "one")
          .build()
      );

  }
}
