package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.exceptions.TwitterReadException;
import com.aazayka.services.TwitterReader;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.interview.oauth.twitter.TwitterAuthenticationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
@Slf4j
public class TwitterAggregator {
    private static final int MESSAGE_COUNT_LIMIT = 50;
    private static final int TIME_LIMIT_MILLIS = 30_000;
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();

    private final TwitterReader twitterReader;
    private final PrintStream writer;


    public void process() throws TwitterReadException, TwitterAuthenticationException, IOException {
        final long[] lastMessageSentTime = {System.currentTimeMillis()};
        final int[] messageCount = {0};
        log.debug("Start reading messages");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(twitterReader.getContent()))) {
            final long start = System.currentTimeMillis();
            log.debug("Start time: {}", start);
            reader.lines()
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
                    .filter(message -> message != null)
                    .collect(groupingBy(
                            Message::getAuthor,
                            () -> new TreeMap<>(
                                    Comparator.comparing(Author::getCreated)
                                            .thenComparing(Author::getId)),
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Message::getTimestamp)))
                    ))
            .forEach(this::printByAuthor);
            log.debug("Message count: {}; finish time, ms: {}", messageCount[0], System.currentTimeMillis());
        }
    }

    void printByAuthor(Author author, Collection<Message> messages) {
        GSON_BUILDER.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = GSON_BUILDER.create();

        JsonObject json = new JsonObject();
        json.add("author", gson.toJsonTree(author));
        JsonArray msgJson = gson.toJsonTree(messages,
                new TypeToken<Collection<Message>>() {
                }.getType()).getAsJsonArray();
        json.add("messages", msgJson);

        writer.println(json);
    }
}
