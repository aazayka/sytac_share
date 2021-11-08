package com.aazayka.services;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ResultPrinterImplTest {

    private static ResultPrinter printer;
    private static ByteArrayOutputStream redirectedOut;

    @BeforeAll
    static void setupPrinter() {
        redirectedOut = new ByteArrayOutputStream();
        printer = new ResultPrinterImpl();
        printer.setWriter(new PrintStream(redirectedOut));
    }

    @SneakyThrows
    @Test
    void print() {
        Author author = new Author(1L, 2L, "name", "screenName");
        //Take some ordered collection
        Collection<Message> messages = new LinkedList<>(List.of(
                new Message(11, 12, "message text", author),
                new Message(21, 22, "message text", author)));
        Map<Author, Collection<Message>> messagesByAuthor = new HashMap<>();
        messagesByAuthor.put(author, messages);
        printer.print(messagesByAuthor);

        // In Java 15 it should be much better
        String expected = "{\n" +
                "    \"author\": {\n" +
                "        \"id\": 1,\n" +
                "        \"created\": 2,\n" +
                "        \"name\": \"name\",\n" +
                "        \"screenName\": \"screenName\"\n" +
                "    },\n" +
                "    \"messages\": [{\n" +
                "            \"id\": 11,\n" +
                "            \"timestamp\": 12,\n" +
                "            \"text\": \"message text\",\n" +
                "            \"author\": {\n" +
                "                \"id\": 1,\n" +
                "                \"created\": 2,\n" +
                "                \"name\": \"name\",\n" +
                "                \"screenName\": \"screenName\"\n" +
                "            }\n" +
                "        }, {\n" +
                "            \"id\": 21,\n" +
                "            \"timestamp\": 22,\n" +
                "            \"text\": \"message text\",\n" +
                "            \"author\": {\n" +
                "                \"id\": 1,\n" +
                "                \"created\": 2,\n" +
                "                \"name\": \"name\",\n" +
                "                \"screenName\": \"screenName\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expected, redirectedOut.toString(), true);
    }
}