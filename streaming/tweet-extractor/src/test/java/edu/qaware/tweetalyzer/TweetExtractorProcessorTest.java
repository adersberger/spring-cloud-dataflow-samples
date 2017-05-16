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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)

public class TweetExtractorProcessorTest {

    @Autowired
    private Processor processor;
    @Autowired
    private MessageCollector collector;

    public static final String SMALL_SAMPLE = "./tweets-sample-1000.log";


    @Test
    public void testExtractor() throws URISyntaxException, IOException, InterruptedException {
        Iterator<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource(SMALL_SAMPLE).toURI())).iterator();

        while(lines.hasNext()){
            String line = lines.next();
            String tweet = line.substring(line.indexOf('{'), line.length()-1);
            checkFor(tweet);
        }

    }

    private void checkFor(String msg) throws InterruptedException {
        processor.input().send(new GenericMessage<>(msg));
        Message<String> out = (Message<String>)collector.forChannel(processor.output()).take();
        assertEquals(TweetHelper.extractTweetText(msg), out.getPayload());
        System.out.println(out.getPayload().toString());

    }

}
