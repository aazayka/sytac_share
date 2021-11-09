package com.aazayka.services;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class ResultPrinterImpl implements ResultPrinter {
    private PrintStream writer;

    private static final Gson gson = new GsonBuilder().create();

    public void setWriter(PrintStream writer) {
        this.writer = writer;
    }

    @Override
    public void print(Map<Author, Collection<Message>> messages) {
        messages.forEach(this::printByAuthor);
    }

    private void printByAuthor(Author author, Collection<Message> messages) {
        JsonObject json = new JsonObject();
        json.add("author", gson.toJsonTree(author));
        JsonArray msgJson = gson.toJsonTree(messages,
                new TypeToken<Collection<Message>>() {
                }.getType()).getAsJsonArray();
        json.add("messages", msgJson);

        writer.println(json);
    }

}
