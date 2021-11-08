package com.aazayka.entities;

import com.aazayka.services.DateConverter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Value;

@Value
public class Message {
    private static DateConverter dateConverter = new DateConverter();
    long id;
    long timestamp;
    String text;
    Author author;

    public static Message createFromJson(String inputStr) {
        // TODO: is it possible to create the class directly from Gson().fromJson()?
        JsonObject inputObj = new Gson().fromJson(inputStr, JsonObject.class);
        return new Message(
                inputObj.get("id").getAsLong(),
                inputObj.get("timestamp_ms").getAsLong() / 1000,
                inputObj.get("text").getAsString(),
                // TODO:Cache and return authors?
                new Author(
                        inputObj.get("user").getAsJsonObject().get("id").getAsLong(),
                        dateConverter.convertToEpochSeconds(inputObj.get("user").getAsJsonObject().get("created_at").getAsString()),
                        inputObj.get("user").getAsJsonObject().get("name").getAsString(),
                        inputObj.get("user").getAsJsonObject().get("screen_name").getAsString()

                )
        );
    }
}
