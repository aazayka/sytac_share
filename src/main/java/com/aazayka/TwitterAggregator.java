package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.exceptions.TwitterReadException;
import com.aazayka.services.TwitterReader;
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
    public static final int MESSAGE_COUNT_LIMIT = 100;
    public static final int TIME_LIMIT_MILLIS = 30_000;

    private final TwitterReader twitterReader;

    public Map<Author, Collection<Message>> collect() throws TwitterReadException, TwitterAuthenticationException, IOException {
        final long[] lastMessageSentTime = {System.currentTimeMillis()};
        final int[] messageCount = {0};
        log.debug("Start reading messages");
        TreeMap<Author, Collection<Message>> messagesByAuthor;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(twitterReader.getContent()))) {
            final long start = System.currentTimeMillis();
            log.debug("Start time: {}", start);
            messagesByAuthor = reader.lines()
                    //Not fare timeout :)
                    .takeWhile((tmp) -> System.currentTimeMillis() <= start + TIME_LIMIT_MILLIS)
                    .limit(MESSAGE_COUNT_LIMIT)
                    //this is not exactly what required in task, but still some message rate
                    .peek((msg) -> {
                        log.debug("Msg rate (msg per sec): {}",
                                String.format("%.3f",
                                    (float) 1000 / (System.currentTimeMillis() - lastMessageSentTime[0])));
                        lastMessageSentTime[0] = System.currentTimeMillis();
                        messageCount[0]++;
                        log.debug(msg);
                    })
                    .map(Message::createFromJson)
                    .filter(Objects::nonNull)
                    .collect(groupingBy(
                            Message::getAuthor,
                            () -> new TreeMap<>(
                                    Comparator.comparing(Author::getCreated)
                                            .thenComparing(Author::getId)),
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Message::getTimestamp)))
                    ));

            log.debug("Message count: {}; finish time, ms: {}", messageCount[0], System.currentTimeMillis());
        }
        return messagesByAuthor;
    }
}
