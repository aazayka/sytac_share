package com.aazayka;

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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
        twitterAggregator = new TwitterAggregator(producer, new PrintStream(redirectedOut));
    }

    @BeforeEach
    void clearProducer() {
        producerSource = new StringBuilder();
    }

    @SneakyThrows
    @Test
    void givenNormalMap_whenCollect_ThenOk() {
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

        twitterAggregator.collect();

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
        twitterAggregator.collect();
        assertEquals("", redirectedOut.toString());
    }



    @Test
    void compareCollections() {
        Map<String, Set<Integer>> map1 = new TreeMap<>();
        map1.put("2", new TreeSet<>(Set.of(3,2,1)));
        map1.put("1", new TreeSet<>(Set.of(1,2,3)));

        Map<String, Set<Integer>> map2 = new TreeMap<>();
        map2.put("1", new TreeSet<>(Set.of(2,1,3)));
        map2.put("3", new TreeSet<>(Set.of(1,2,3)));

        assertEquals(map1, map2);
    }
}