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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
public class TwitterAggregator {


    private static final String CONSUMER_KEY = "RLSrphihyR4G2UxvA0XBkLAdl";
    private static final String CONSUMER_SECRET = "FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4";
    public static final int MESSAGE_COUNT_LIMIT = 50;
    public static final int TIME_LIMIT = 30_000;
    public static final String BIBER_URL = "https://stream.twitter.com/1.1/statuses/filter.json?track=bieber";


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
            final Function<JsonObject, JsonObject> twitterConverter = (JsonObject obj) -> {
                JsonObject out = new JsonObject();
                out.addProperty("id", obj.get("id").getAsLong());
                out.addProperty("timestamp", obj.get("timestamp_ms").getAsLong() / 1000);
                out.addProperty("text", obj.get("text").getAsString());
                JsonObject author = obj.get("user").getAsJsonObject();
                out.addProperty("author_id", author.get("id").getAsLong());
                //TODO: convert to epoch
                //out.addProperty("created_at", author.get("created_at").getAsString());
                out.addProperty("created_at", obj.get("timestamp_ms").getAsLong() / 1000);
                out.addProperty("name", author.get("name").getAsString());
                out.addProperty("screen_name", author.get("screen_name").getAsString());
                return out;
            };

//            Map<Author, Set<Message>> map =
            reader.lines()
                    .takeWhile((tmp) -> System.currentTimeMillis() <= start + TIME_LIMIT)
                    .limit(MESSAGE_COUNT_LIMIT)
                    .peek((msg) -> {
                        System.out.printf("Msg rate (msg per sec): %.3f%n", (double) 1000 / (System.currentTimeMillis() - last_message_sent_time[0]));
                        last_message_sent_time[0] = System.currentTimeMillis();
                        System.out.println(msg);
                    })
                    .map(str -> new Gson().fromJson(str, JsonObject.class))
                    .map(twitterConverter)
                    .collect(groupingBy(
                            getAuthor(),
                            () -> new TreeMap<>(Comparator.comparing(Author::getCreated).thenComparing(Author::getId)),
                            Collectors.mapping(
                                    getMessage(),
                                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Message::getTimestamp)))
                            )
                    ))
                    .forEach(TwitterAggregator::printMapInstance)
            ;
        }
    }

    private static Function<JsonObject, Message> getMessage() {
        return (JsonObject obj) -> new Message(
                obj.get("id").getAsLong(),
                obj.get("timestamp").getAsLong(),
                obj.get("text").getAsString(),
                obj.get("author_id").getAsLong());
    }

    private static Function<JsonObject, Author> getAuthor() {
        return (JsonObject obj) -> new Author(
                obj.get("author_id").getAsLong(),
                obj.get("created_at").getAsLong(),
                obj.get("name").getAsString(),
                obj.get("screen_name").getAsString());
    }

    private static void printMapInstance(Author author, TreeSet<Message> msgList) {
        JsonObject json = (new Gson()).toJsonTree(author, Author.class).getAsJsonObject();
        JsonArray msgJson = new Gson().toJsonTree(msgList,
                new TypeToken<Set<Message>>() {
                }.getType()).getAsJsonArray();
        json.add("messages", msgJson);
        System.out.println(json.toString());
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
        long authorId;
    }

}
