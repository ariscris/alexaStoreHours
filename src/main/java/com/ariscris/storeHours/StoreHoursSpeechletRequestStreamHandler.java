
package com.ariscris.storeHours;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public final class StoreHoursSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();
    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds.add("amzn1.ask.skill.9e44f8a0-1edc-415c-9bdf-e4c56544e0b4");
    }

    public StoreHoursSpeechletRequestStreamHandler() {
        super(new StoreHoursSpeechlet(), supportedApplicationIds);
    }
}
