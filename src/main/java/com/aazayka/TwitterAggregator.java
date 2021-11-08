package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.exceptions.TwitterReadException;
import com.aazayka.services.TwitterReader;
import com.aazayka.services.TwitterReaderImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.interview.oauth.twitter.TwitterAuthenticationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
@Slf4j
public class TwitterAggregator {
    private static final int MESSAGE_COUNT_LIMIT = 50;
    private static final int TIME_LIMIT = 30_000;

    TwitterReader twitterReader;

    public Map<Author, Collection<Message>> collect() throws TwitterReadException, TwitterAuthenticationException, IOException {
        final long[] last_message_sent_time = {System.currentTimeMillis()};
        final long start = System.currentTimeMillis();
        Map<Author, Collection<Message>> messagesByAuthor;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(twitterReader.getContent()))) {
            messagesByAuthor = reader.lines()
                    .takeWhile((tmp) -> System.currentTimeMillis() <= start + TIME_LIMIT)
                    .limit(MESSAGE_COUNT_LIMIT)
                    //this is not exactly what required in task, but keep it for fun
                    .peek((msg) -> {
                        log.debug("Msg rate (msg per sec): {}",
                                Math.round((float) 1000 / (System.currentTimeMillis() - last_message_sent_time[0])));
                        last_message_sent_time[0] = System.currentTimeMillis();
                        log.debug(msg);
                    })
                    .map(Message::createFromJson)
                    .collect(groupingBy(
                            Message::getAuthor,
                            () -> new TreeMap<>(
                                    Comparator.comparing(Author::getCreated)
                                            .thenComparing(Author::getId)),
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Message::getTimestamp)))
                    ));

        }
        return messagesByAuthor;
    }
}
