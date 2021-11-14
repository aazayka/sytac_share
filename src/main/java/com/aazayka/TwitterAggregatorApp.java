package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.exceptions.TwitterReadException;
import com.aazayka.services.ResultPrinterImpl;
import com.aazayka.services.TwitterReaderImpl;
import org.interview.oauth.twitter.TwitterAuthenticationException;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

public class TwitterAggregatorApp {

    private static final String OUTPUT_FILE_NAME = "out.txt"; // to System.out if empty
    public static final int MESSAGE_COUNT_LIMIT = 100;
    public static final int TIME_LIMIT_MILLIS = 30_000;


    public static void main(String[] args) throws IOException, TwitterReadException, TwitterAuthenticationException {
        TwitterAggregator twitterAggregator = new TwitterAggregator(new TwitterReaderImpl());
        Map<Author, Collection<Message>> messages = twitterAggregator.collect();
        //new ResultPrinterImpl(System.out).print(messages);
        PrintStream outputStream = OUTPUT_FILE_NAME.isEmpty() ?
                System.out :
                new PrintStream(new BufferedOutputStream(new FileOutputStream(OUTPUT_FILE_NAME)));
        new ResultPrinterImpl(outputStream).print(messages);

        //TODO: save message rate
    }

}
