package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.exceptions.TwitterReadException;
import com.aazayka.services.ResultPrinter;
import com.aazayka.services.TwitterReaderImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.interview.oauth.twitter.TwitterAuthenticationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class TwitterAggregator {
    private static final int MESSAGE_COUNT_LIMIT = 50;
    private static final int TIME_LIMIT = 30_000;

    TwitterReaderImpl twitterReader;
    ResultPrinter printer;

    public void process() throws TwitterReadException, TwitterAuthenticationException, IOException {
        final long[] last_message_sent_time = {System.currentTimeMillis()};
        final long start = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(twitterReader.getContent()))) {
            reader.lines()
                    .takeWhile((tmp) -> System.currentTimeMillis() <= start + TIME_LIMIT)
                    .limit(MESSAGE_COUNT_LIMIT)
                    .peek((msg) -> {
                        System.out.printf("Msg rate (msg per sec): %.3f%n", (double) 1000 / (System.currentTimeMillis() - last_message_sent_time[0]));
                        last_message_sent_time[0] = System.currentTimeMillis();
                        System.out.println(msg);
                    })
                    .map(Message::createFromJson)
                    .collect(groupingBy(
                            Message::getAuthor,
                            () -> new TreeMap<>(
                                    Comparator.comparing(Author::getCreated)
                                            .thenComparing(Author::getId)),
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Message::getTimestamp)))
                    ))
                    .forEach(printer::print)
            ;
        }
    }
}
