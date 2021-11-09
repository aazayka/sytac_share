package com.aazayka.entities;

import com.aazayka.services.DateConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
public class Message {
    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private static DateConverter dateConverter = new DateConverter();
    long id;
    long timestamp;
    String text;
    @SerializedName(value = "author", alternate="user")
    Author author;

    public static Message createFromJson(String inputStr) {
        JsonObject inputObj = (new Gson()).fromJson(inputStr, JsonObject.class);
        if (inputObj.get("user") == null)
            return null;
        JsonObject authorObject = inputObj.get("user").getAsJsonObject();

        Message message =
                new Message(
                        inputObj.get("id").getAsLong(),
                        inputObj.get("timestamp_ms").getAsLong() / 1000,
                        inputObj.get("text").getAsString(),
                        new Author(
                                authorObject.get("id").getAsLong(),
                                dateConverter.convertToEpochSeconds(authorObject.getAsJsonObject().get("created_at").getAsString()),
                                authorObject.get("name").getAsString(),
                                authorObject.get("screen_name").getAsString()

                        )
                );
        log.debug(message.toString());
        return message;
    }

}
