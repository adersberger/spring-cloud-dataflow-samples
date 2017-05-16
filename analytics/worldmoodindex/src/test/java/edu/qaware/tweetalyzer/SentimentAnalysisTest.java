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

import com.twitter.Extractor;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SentimentAnalysisTest {

    public static final String SMALL_SAMPLE = "./tweets-sample-1000.log";
    public static final String LARGE_SAMPLE = "tweets-sample-1000.log";

    @Test
    public void testAnalysis() throws URISyntaxException, IOException {

        StanfordCoreNLP pipeline = new StanfordCoreNLP("stanford-nlp.properties");
        Stream<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource(SMALL_SAMPLE).toURI()));

        Map<Integer, BigInteger> histogram = new HashMap<>();
        int i = 0;

        for (String line : lines.collect(Collectors.toList())){
            String tweetText = extractTweetText(line);
            int sentiment = findSentiment(pipeline, tweetText);
            System.out.println(sentiment + ": " + tweetText);
            if (!histogram.containsKey(Integer.valueOf(sentiment))) histogram.put(sentiment, BigInteger.ONE);
            else {
                histogram.put(sentiment, histogram.get(sentiment).add(BigInteger.ONE));
            }
        }

        for (Map.Entry<Integer, BigInteger> entry : histogram.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
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

    public static String extractTweetText(String line) {
        //extract tweet JSON
        String tweet = line.substring(line.indexOf('{'), line.length()-1);
        final String START = "\"text\":\"";
        String tweetTextFirst = tweet.substring(tweet.indexOf(START) + START.length(), tweet.length()-1);
        String tweetText = tweetTextFirst.substring(0, tweetTextFirst.indexOf('"'));

        //eliminate screen names
        Extractor extractor = new Extractor();
        List<String> names = extractor.extractMentionedScreennames(tweetText);
        for (String name : names){
            tweetText = tweetText.replace((CharSequence) name, "");
        }

        //eliminate standard syntax
        tweetText = tweetText.replace("RT ", "");
        tweetText = tweetText.replace("#", "");
        tweetText = tweetText.replace("@", "");
        tweetText = tweetText.replace(": ", "");

        //eliminate links
        tweetText = tweetText.replaceAll("https:[^ ]*", "");

        //eliminate unicode chars and non-printable chars (e.g. emojis).
        //Remember to detect sentiment based on emojis in an upcoming version.
        tweetText = tweetText.replaceAll("\\\\u[a-z0-9]{4}", "");
        tweetText = tweetText.replaceAll("\\\\n", " ");
        tweetText = tweetText.replaceAll("\\\\", "");
        tweetText = tweetText.replaceAll("&[a-z]*;", " ");
        tweetText = tweetText.trim();

        return tweetText;
    }

}
