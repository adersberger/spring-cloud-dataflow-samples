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

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
@Component
/**
 * Provides access to a StanfordNLP processing pipeline.
 *
 * Processes and annotates a given text
 */
public class StanfordNLP {

    private StanfordCoreNLP pipeline; //StanfordNLP is thread-safe

    public StanfordNLP() {
        pipeline = new StanfordCoreNLP("stanford-nlp.properties");
    }

    /**
     * Annotates a text with the annotators defined in stanford-nlp.properties
     *
     * @param text the text to be annotated
     * @return annotated text
     */
    public Annotation process(String text) {
        return pipeline.process(text);
    }

}