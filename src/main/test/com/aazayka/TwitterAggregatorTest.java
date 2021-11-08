package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.services.TwitterReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class TwitterAggregatorTest {

    private static TwitterReader producer;
    private static StringBuilder producerSource;
    private static ByteArrayOutputStream redirectedOut;
    private static TwitterAggregator twitterAggregator;

    @BeforeAll
    static void setupPrinter() {
        producer = () -> new ByteArrayInputStream(producerSource.toString().getBytes());

        redirectedOut = new ByteArrayOutputStream();
        twitterAggregator = new TwitterAggregator(producer, new PrintStream(redirectedOut));
    }

    @BeforeEach
    void clearProducer() {
        producerSource = new StringBuilder();
    }

    @SneakyThrows
    @Test
    void process() {
        String twitterResponse;

        final ZonedDateTime author1CreationDate = ZonedDateTime.of(
                2020, 12, 3, 12, 20, 59,
                90000, ZoneId.systemDefault());
        final ZonedDateTime author2CreationDate = author1CreationDate.minusDays(1);

        twitterResponse = new TestData()
                .withAuthor(1, "Place 2", author1CreationDate)
                .withMessageId(1)
                .withMessageTs(1)
                .withMessageText("First message")
                .getMessage();
        producerSource.append(twitterResponse);

        twitterResponse = new TestData()
                .withAuthor(1, "Place 2", author1CreationDate)
                .withMessageId(2)
                .withMessageTs(2)
                .withMessageText("Second message")
                .getMessage();
        producerSource.append(twitterResponse);

        twitterResponse = new TestData()
                .withAuthor(2, "Place 1", author2CreationDate)
                .withMessageId(2)
                .withMessageTs(2)
                .withMessageText("Second message - author 2")
                .getMessage();
        producerSource.append(twitterResponse);

        twitterResponse = new TestData()
                .withAuthor(2, "Place 1", author2CreationDate)
                .withMessageId(1)
                .withMessageTs(1)
                .withMessageText("First message - author 2")
                .getMessage();
        producerSource.append(twitterResponse);

        twitterAggregator.process();

        final String expected = "{'author':{'id':2,'createdAt':'Wed Dec 2 12:20:59 +0100 2020','name':'Place 1','screenName':'Place 1'},'messages':[{'id':1,'timestampMs':1000,'text':'First message - author 2','user':{'id':2,'createdAt':'Wed Dec 2 12:20:59 +0100 2020','name':'Place 1','screenName':'Place 1'}},{'id':2,'timestampMs':2000,'text':'Second message - author 2','user':{'id':2,'createdAt':'Wed Dec 2 12:20:59 +0100 2020','name':'Place 1','screenName':'Place 1'}}]}\n" +
                "{'author':{'id':1,'createdAt':'Thu Dec 3 12:20:59 +0100 2020','name':'Place 2','screenName':'Place 2'},'messages':[{'id':1,'timestampMs':1000,'text':'First message','user':{'id':1,'createdAt':'Thu Dec 3 12:20:59 +0100 2020','name':'Place 2','screenName':'Place 2'}},{'id':2,'timestampMs':2000,'text':'Second message','user':{'id':1,'createdAt':'Thu Dec 3 12:20:59 +0100 2020','name':'Place 2','screenName':'Place 2'}}]}\n";
        //assertEquals("x", redirectedOut.toString());
        log.debug(redirectedOut.toString());
        JSONAssert.assertEquals("{}", redirectedOut.toString(), true);
    }

    @SneakyThrows
    @Test
    void givenTwitterAggregator_whenWrongJsonIn_thenEmptyOut() {

        producerSource.append("{'dummyAttribute': 1}");
        twitterAggregator.process();
        assertEquals("", redirectedOut.toString());
    }

    @SneakyThrows
    @Test
    void print() {
        Author author = new Author(1L, "Wed Nov 03 13:25:20 +0000 2021", "name", "screenName");
        //Take some ordered collection
        Collection<Message> messages = new LinkedList<>(List.of(
                new Message(11, 12, "message text", author),
                new Message(21, 22, "message text", author)));
        twitterAggregator.printByAuthor(author, messages);

        // In Java 15 it should be much better
        String expected = "{\n" +
                "    \"author\": {\n" +
                "        \"id\": 1,\n" +
                "        \"created\": 2,\n" +
                "        \"name\": \"name\",\n" +
                "        \"screenName\": \"screenName\"\n" +
                "    },\n" +
                "    \"messages\": [{\n" +
                "            \"id\": 11,\n" +
                "            \"timestamp\": 12,\n" +
                "            \"text\": \"message text\",\n" +
                "            \"author\": {\n" +
                "                \"id\": 1,\n" +
                "                \"created\": 2,\n" +
                "                \"name\": \"name\",\n" +
                "                \"screenName\": \"screenName\"\n" +
                "            }\n" +
                "        }, {\n" +
                "            \"id\": 21,\n" +
                "            \"timestamp\": 22,\n" +
                "            \"text\": \"message text\",\n" +
                "            \"author\": {\n" +
                "                \"id\": 1,\n" +
                "                \"created\": 2,\n" +
                "                \"name\": \"name\",\n" +
                "                \"screenName\": \"screenName\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        assertEquals(expected, redirectedOut.toString());
        //JSONAssert.assertEquals(expected, redirectedOut.toString(), true);
    }
}