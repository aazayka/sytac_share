package com.aazayka.services;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.List;

public class ResultPrinterImpl implements ResultPrinter {
    @Override
    public void print(Author author, Collection<Message> messages) {
        JsonObject json = new JsonObject();
        json.add("author", (new Gson()).toJsonTree(author));
        JsonArray msgJson = new Gson().toJsonTree(messages,
                new TypeToken<Collection<Message>>() {
                }.getType()).getAsJsonArray();
        json.add("messages", msgJson);
        System.out.println(json);
    }
}
