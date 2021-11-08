package com.aazayka.services;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class ResultPrinterImpl implements ResultPrinter {
    private PrintStream writer = System.out;

    public void setWriter(PrintStream writer) {
        this.writer = writer;
    }

    @Override
    public void print(Map<Author, Collection<Message>> messages) {
        messages.forEach(this::printByAuthor);
    }

    private void printByAuthor(Author author, Collection<Message> messages) {

        JsonObject json = new JsonObject();
        json.add("author", (new Gson()).toJsonTree(author));
        JsonArray msgJson = new Gson().toJsonTree(messages,
                new TypeToken<Collection<Message>>() {
                }.getType()).getAsJsonArray();
        json.add("messages", msgJson);

        writer.println(json);
    }
}
