package com.aazayka.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageRateServiceImplTest {

    private static float getRate(String input) {
        JsonObject inObject= new Gson().fromJson(input, JsonObject.class);
        return inObject.get("rate").getAsFloat();
    }

    @Test
    void positiveScanario() {
            MessageRateService messageRateService = new MessageRateServiceImpl();
            messageRateService.start();
            assertNotNull(messageRateService.toString());

            messageRateService.incrementMessageCounter();
            assertNotNull(messageRateService.toString());

            messageRateService.finish();

            assertTrue(getRate(messageRateService.toString()) > 0);

    }

    @Test
    void givenService_whenNotStarted_thenToStringIsNull() {
        MessageRateService messageRateService = new MessageRateServiceImpl();
        assertNull(messageRateService.toString());
    }

    @Test
    void givenService_whenFinishNotStarted_thenException() {
        MessageRateService messageRateService = new MessageRateServiceImpl();
        assertThrows(RuntimeException.class, () -> messageRateService.finish());
    }
}