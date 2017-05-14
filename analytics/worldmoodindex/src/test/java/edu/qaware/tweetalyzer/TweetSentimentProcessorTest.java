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
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TweetSentimentProcessorTest {

    @Autowired
    private Processor processor;
    @Autowired
    private MessageCollector collector;
    @Autowired
    private TweetSentimentProcessor sentimentProcessor;

    @Test
    public void testWiring() {
        checkFor("I hate everybody around me!");
        checkFor("The world is lovely");
        checkFor("I f***ing hate everybody around me. They're from hell");
        checkFor("Sunny day today!");
    }

    private void checkFor(String msg) {
        processor.input().send(new GenericMessage<>(msg));
        assertThat(
                collector.forChannel(processor.output()),
                receivesPayloadThat(
                        equalTo("{\"mood\": \"" + sentimentProcessor.findSentiment(msg) + "\"}"))
        );
    }

}
