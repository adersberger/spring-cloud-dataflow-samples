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
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

public class SentimentAnalysisTest {

    @Test
    public void testAnalysis() {
        for (int i = 0; i < 1000; i++) {
            String tweet = "I hate everybody around me!";
            String tweet2 = "The world is lovely and the best world imaginable!";
            String tweet3 = "I fucking hate everybody around me. They're from hell and morons!";
            String tweet4 = "Sunny day today!";

            StanfordCoreNLP pipeline = new StanfordCoreNLP("stanford-nlp.properties");

            int sentiment = findSentiment(pipeline, tweet);
            System.out.println(sentiment);

            int sentiment2 = findSentiment(pipeline, tweet2);
            System.out.println(sentiment2);

            int sentiment3 = findSentiment(pipeline, tweet3);
            System.out.println(sentiment3);

            int sentiment4 = findSentiment(pipeline, tweet4);
            System.out.println(sentiment4);
        }
    }


    /**
     * https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/sentiment/package-summary.html
     *
     * @param pipeline
     * @param tweet
     * @return
     */
    public int findSentiment(StanfordCoreNLP pipeline, String tweet) {

        int mainSentiment = 0;
        if (tweet != null && tweet.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(tweet);
            for (CoreMap sentence : annotation
                    .get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence
                        .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
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

}
