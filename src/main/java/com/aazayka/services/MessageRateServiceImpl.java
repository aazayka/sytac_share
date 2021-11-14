package com.aazayka.services;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MessageRateServiceImpl implements MessageRateService {
    private volatile long start;
    private volatile long finish;

    private AtomicInteger messageCount = new AtomicInteger();
    @Override
    synchronized public void start() {
        //if start is not initialized from other thread - init it
        if (start == 0L) {
            this.start = System.currentTimeMillis();
            log.debug("Start time: {}", this.start);
        }
    }

    @Override
    public void finish() {
        //finish keeps last finish time
        if (!isStarted()) {
            throw new RuntimeException("Trying to finish MessageRateService that is not started");
        }

        this.finish = System.currentTimeMillis();
        log.debug("End time: {}, message count={}", this.finish, this.messageCount.get());
    }

    @Override
    public void incrementMessageCounter() {
        messageCount.incrementAndGet();
    }

    private boolean isStarted() {
        return this.start > 0L;
    }
    @Override
    public String toString() {
        if (!isStarted() || this.start == this.finish /*Too fast*/) {
            return null;
        }

        JsonObject out = new JsonObject();
        out.addProperty("rate", (float) this.messageCount.get() / (this.finish - this.start));
        log.debug(out.toString());
        return out.toString();
    }
}
