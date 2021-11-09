package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.exceptions.TwitterReadException;
import com.aazayka.services.ResultPrinterImpl;
import com.aazayka.services.TwitterReaderImpl;
import org.interview.oauth.twitter.TwitterAuthenticationException;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class TwitterAggregatorApp {

    public static void main(String[] args) throws IOException, TwitterReadException, TwitterAuthenticationException {
        TwitterAggregator twitterAggregator = new TwitterAggregator(new TwitterReaderImpl());
        Map<Author, Collection<Message>> messages = twitterAggregator.collect();
        new ResultPrinterImpl(System.out).print(messages);

        //TODO: add Docker support

        //TODO: save message rate

        //TODO: check output to file
    }

}
