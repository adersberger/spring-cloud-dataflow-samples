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

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@EnableBinding(Processor.class)
/**
 * Detects the sentiment of incoming tweets and produces JSON output
 * with the sentiment index in the form of {mood: [sentiment]}
 * Index definition:
 * <ul>
 * <li>0 = very negative</li>
 * <li>1 = negative</li>
 * <li>2 = neutral</li>
 * <li>3 = positive</li>
 * <li>4 = very positive</li>
 * </ul>
 */
public class TweetSentimentProcessor {

    @Autowired
    StanfordNLP nlp;

    @StreamListener(Processor.INPUT) //input channel with default name
    @SendTo(Processor.OUTPUT)        //output channel with default name
    /**
     * Spring Cloud Stream processor method for tweet sentiment analysis
     */
    public String analyzeSentiment(String tweet){

        String msg = "{\"mood\": \"" + findSentiment(tweet) + "\"}";
        System.out.println(msg);
        return msg;
    }

    /**
     * Method to detect tweet sentiment by using StanfordNLP.
     *
     * see https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/sentiment/package-summary.html
     *
     * @param tweet the tweet text
     * @return the sentiment (see class-level documentation for value explanation)
     */
    public int findSentiment(String tweet) {
      int mainSentiment = 0;
        if (tweet != null && tweet.length() > 0) {
            int longest = 0;
            Annotation annotation = nlp.process(tweet);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }
        return mainSentiment;
    }

    public static void main(String[] args) {
        SpringApplication.run(TweetSentimentProcessor.class, args);
    }
}
