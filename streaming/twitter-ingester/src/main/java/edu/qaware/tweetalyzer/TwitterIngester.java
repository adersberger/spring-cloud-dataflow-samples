/*
 * Copyright (C) 2016 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package edu.qaware.tweetalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

@SpringBootApplication
@EnableBinding(Source.class)
/**
 * Ingests twitter data at a certain rate
 */
public class TwitterIngester {

    public static final String LARGE_SAMPLE = "tweets-sample-10000.log";

    private Iterator<String> lines;

    public TwitterIngester() throws URISyntaxException, IOException {
        lines = readTweets();
    }

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = @Poller(fixedDelay = "200", maxMessagesPerPoll = "1"))
    public MessageSource<String> timerMessageSource() {
        return () -> new GenericMessage<>(emitTweet());
    }

    private String emitTweet() {
        if (!lines.hasNext()) lines = readTweets();
        return lines.next();
    }

    private Iterator<String> readTweets() {
        try {
            return Files.lines(Paths.get(ClassLoader.getSystemResource(LARGE_SAMPLE).toURI())).iterator();
        } catch (IOException|URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractTweetJson(String line){
        return line.substring(line.indexOf('{'), line.length()-1);
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterIngester.class, args);
    }

}
