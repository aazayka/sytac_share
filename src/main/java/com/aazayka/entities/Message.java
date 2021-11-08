package com.aazayka.entities;

import com.aazayka.services.DateConverter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
public class Message {
    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private static DateConverter dateConverter = new DateConverter();
    long id;
    long timestampMs;
    String text;
    @SerializedName(value = "author", alternate="user")
    Author author;

    public long getTimestamp() {
        return timestampMs / 1000;
    }

    public static Message createFromJson(String inputStr) {
        // TODO: is it possible to create the class directly from Gson().fromJson()?
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();
        //JsonObject inputObj = gson.fromJson(inputStr, JsonObject.class);
        Message message = gson.fromJson(inputStr, Message.class);
        log.debug(message.toString());
        return message.author == null ? null : message;

//        return new Message(
//                inputObj.get("id").getAsLong(),
//                inputObj.get("timestamp_ms").getAsLong() / 1000,
//                inputObj.get("text").getAsString(),
//                // TODO:Cache and return authors?
//                new Author(
//                        inputObj.get("user").getAsJsonObject().get("id").getAsLong(),
//                        dateConverter.convertToEpochSeconds(inputObj.get("user").getAsJsonObject().get("created_at").getAsString()),
//                        inputObj.get("user").getAsJsonObject().get("name").getAsString(),
//                        inputObj.get("user").getAsJsonObject().get("screen_name").getAsString()
//
//                )
//        );
    }
}
