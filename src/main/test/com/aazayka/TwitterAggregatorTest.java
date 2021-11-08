package com.aazayka;

import com.aazayka.services.TwitterReader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwitterAggregatorTest {

    TwitterReader producer;

    @SneakyThrows
    @Test
    void process() {
        TwitterAggregator twitterAggregator = new TwitterAggregator(producer);
        twitterAggregator.collect();
    }
}