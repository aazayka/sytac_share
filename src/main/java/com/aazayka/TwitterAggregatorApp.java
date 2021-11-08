package com.aazayka;

import com.aazayka.exceptions.TwitterReadException;
import com.aazayka.services.TwitterReaderImpl;
import org.interview.oauth.twitter.TwitterAuthenticationException;

import java.io.IOException;

public class TwitterAggregatorApp {

    public static void main(String[] args) throws IOException, TwitterReadException, TwitterAuthenticationException {
        TwitterAggregator twitterAggregator = new TwitterAggregator(new TwitterReaderImpl(), System.out);
        twitterAggregator.process();
        //TODO: create Printer class again
        //It accepts where to print and can print collection preserving order

        //TODO: add custom deserializer because of date fields

        //TODO: unitTests - make them convinient

        //TODO: check the naming

        //TODO: add Docker support

        //TODO: save message rate
    }

}
