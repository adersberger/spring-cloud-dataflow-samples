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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TweetHelperTest {

    public static final String SMALL_SAMPLE = "./tweets-sample-1000.log";

    @Test
    public void testAnalysis() throws URISyntaxException, IOException {

        Stream<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource(SMALL_SAMPLE).toURI()));

        for (String line : lines.collect(Collectors.toList())){
            String tweetText = TweetHelper.extractTweetText(line);
            assert(tweetText.length() < line.length());
        }

    }

}
