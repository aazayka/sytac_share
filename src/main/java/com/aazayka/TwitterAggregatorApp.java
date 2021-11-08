package com.aazayka;

import com.aazayka.exceptions.TwitterReadException;
import lombok.extern.slf4j.Slf4j;
import org.interview.oauth.twitter.TwitterAuthenticationException;

import java.io.IOException;

public class TwitterAggregatorApp {

    public static void main(String[] args) throws IOException, TwitterReadException, TwitterAuthenticationException {
        new TwitterAggregator().process();
    }

}
