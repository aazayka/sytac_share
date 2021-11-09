package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.aazayka.services.TwitterReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class TwitterAggregatorTest {

    private static StringBuilder producerSource;
    private static TwitterAggregator twitterAggregator;

    @BeforeAll
    static void setupPrinter() {
        TwitterReader producer = () -> new ByteArrayInputStream(producerSource.toString().getBytes());
        twitterAggregator = new TwitterAggregator(producer);
    }

    @BeforeEach
    void clearProducer() {
        producerSource = new StringBuilder();
    }

    @SneakyThrows
    @Test
    void givenNormalMap_whenCollect_ThenOk() {
        String twitterResponse;

        final Author author1 = new Author(1, 999999, "Place 2", "Place 2");
        final Author author2 = new Author(2, 111111, "Place 1", "Place 1");

        twitterResponse = new TestData()
                .withAuthor(author1)
                .withMessageId(1)
                .withMessageTs(1)
                .withMessageText("First message - author 1")
                .getMessage();
        producerSource.append(twitterResponse);

        twitterResponse = new TestData()
                .withAuthor(author1)
                .withMessageId(2)
                .withMessageTs(2)
                .withMessageText("Second message - author 1")
                .getMessage();
        producerSource.append(twitterResponse);

        twitterResponse = new TestData()
                .withAuthor(author2)
                .withMessageId(2)
                .withMessageTs(2)
                .withMessageText("Second message - author 2")
                .getMessage();
        producerSource.append(twitterResponse);

        twitterResponse = new TestData()
                .withAuthor(author2)
                .withMessageId(1)
                .withMessageTs(1)
                .withMessageText("First message - author 2")
                .getMessage();
        producerSource.append(twitterResponse);

        Map<Author, Collection<Message>> expected = new TreeMap<>(Comparator.comparing(Author::getCreated));

        expected.put(author2,
                Set.of(
                        new Message(1, 1, "First message - author 2", author2),
                        new Message(2, 2, "Second message - author 2", author2)
                ));
        expected.put(author1,
                Set.of(
                        new Message(1, 1, "First message - author 1", author1),
                        new Message(2, 2, "Second message - author 1", author1)
                ));
        assertEquals(expected, twitterAggregator.collect());
    }

    @SneakyThrows
    @Test
    void givenTwitterAggregator_whenWrongJsonIn_thenEmptyOut() {

        producerSource.append("{'dummyAttribute': 1}");
        assertEquals(new TreeMap<>(), twitterAggregator.collect(), "Collect result should be empty");
    }
}