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

import java.util.List;

/**
 * Helper methods for tweet analysis
 */
public class TweetHelper {

    /**
     * Extract text from JSON tweet
     *
     * @param line tweet json structure
     * @return text within tweet
     */
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
