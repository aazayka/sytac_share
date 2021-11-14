package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.exceptions.TwitterReadException;
import com.aazayka.services.MessageRateService;
import com.aazayka.services.MessageRateServiceImpl;
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

    private static final String OUTPUT_FILE_NAME = "/output/out.txt"; // to System.out if empty
    private static final String MESSAGE_RATE_FILE_NAME = "/output/rate.txt"; // to System.out if empty
    public static final int MESSAGE_COUNT_LIMIT = 100;
    public static final int TIME_LIMIT_MILLIS = 30_000;


    public static void main(String[] args) throws IOException, TwitterReadException, TwitterAuthenticationException {
        // Reads for twitter and return stream
        TwitterReaderImpl twitterReader = new TwitterReaderImpl();
        //Keep message rate
        MessageRateService messageRateService = new MessageRateServiceImpl();

        TwitterAggregator twitterAggregator = new TwitterAggregator(twitterReader, messageRateService);

        Map<Author, Collection<Message>> messages = twitterAggregator.collect();

        // Print result
        PrintStream outputStream = OUTPUT_FILE_NAME.isEmpty() ?
                System.out :
                new PrintStream(new BufferedOutputStream(new FileOutputStream(OUTPUT_FILE_NAME)));
        new ResultPrinterImpl(outputStream).print(messages);

        //TODO: save message rate


    }

}
