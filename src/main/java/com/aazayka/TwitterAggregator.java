package com.aazayka;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.interview.oauth.twitter.TwitterAuthenticationException;
import org.interview.oauth.twitter.TwitterAuthenticator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
public class TwitterAggregator {


    private static final String CONSUMER_KEY = "RLSrphihyR4G2UxvA0XBkLAdl";
    private static final String CONSUMER_SECRET = "FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4";
    private static final int MESSAGE_COUNT_LIMIT = 50;
    private static final int TIME_LIMIT = 30_000;
    private static final String BIBER_URL = "https://stream.twitter.com/1.1/statuses/filter.json?track=bieber";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss Z yyyy", Locale.ENGLISH);

    public static void main(String[] args) throws TwitterAuthenticationException, IOException {
        TwitterAuthenticator twitterAuthenticator = new TwitterAuthenticator(System.out, CONSUMER_KEY, CONSUMER_SECRET);
        HttpRequestFactory httpRequestFactory = twitterAuthenticator.getAuthorizedHttpRequestFactory();
        //TODO: check
        HttpRequest request = httpRequestFactory.buildRequest("POST", new GenericUrl(BIBER_URL), null);
        // This timeout does not work as expected
        request.setConnectTimeout(TIME_LIMIT);
        request.setReadTimeout(TIME_LIMIT);

        HttpResponse response = request.execute();

        final long start = System.currentTimeMillis();
        if (response.getStatusCode() != 200) {
            log.error("Invalid response code " + response.getStatusCode());
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent()))) {
            final long[] last_message_sent_time = {System.currentTimeMillis()};

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
                    .forEach(TwitterAggregator::printMapInstance)
            ;
        }
    }

    private static void printMapInstance(Author author, TreeSet<Message> msgList) {
        JsonObject json = new JsonObject();
        json.add("author", (new Gson()).toJsonTree(author));
        JsonArray msgJson = new Gson().toJsonTree(msgList,
                new TypeToken<Set<Message>>() {
                }.getType()).getAsJsonArray();
        json.add("messages", msgJson);
        System.out.println(json);
    }

    @Value
    public static class Author {
        long id;
        long created;
        String name;
        String screenName;
    }

    @Value
    public static class Message {
        long id;
        long timestamp;
        String text;
        Author author;

        static Message createFromJson(String inputStr) {
            // TODO: is it possible to create the class directly from Gson().fromJson()?
            JsonObject inputObj = new Gson().fromJson(inputStr, JsonObject.class);
            return new Message(
                    inputObj.get("id").getAsLong(),
                    inputObj.get("timestamp_ms").getAsLong() / 1000,
                    inputObj.get("text").getAsString(),
                    // TODO:Cache and return authors?
                    new Author(
                            inputObj.get("user").getAsJsonObject().get("id").getAsLong(),
                            convertToEpochSeconds(inputObj.get("user").getAsJsonObject().get("created_at").getAsString()),
                            inputObj.get("user").getAsJsonObject().get("name").getAsString(),
                            inputObj.get("user").getAsJsonObject().get("screen_name").getAsString()

                    )
            );
        }

        private static long convertToEpochSeconds(String dateTimeStr) {
            return ZonedDateTime.parse(dateTimeStr, DATE_FORMATTER).toEpochSecond();
        }
    }

}
