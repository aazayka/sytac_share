package com.aazayka.services;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

class ResultPrinterImplTest {
    private static ResultPrinter printer;
    private static ByteArrayOutputStream redirectedOut;

    @BeforeAll
    static void beforeAll() {
        redirectedOut = new ByteArrayOutputStream();
        printer = new ResultPrinterImpl(new PrintStream(redirectedOut));
    }

    @SneakyThrows
    @Test
    void givenRegularMap_whenPrint_ThenOk() {
        Author author = new Author(1L, 2, "name", "screenName");
        //Take some ordered collection
        Collection<Message> messages = new LinkedList<>(List.of(
                new Message(11, 12, "message text", author),
                new Message(21, 22, "message text", author)));
        Map<Author, Collection<Message>> messagesByAuthor = new HashMap<>(  );
        messagesByAuthor.put(author, messages);
        printer.print(messagesByAuthor);

        String expected = "{\n" +
                "    'author': {\n" +
                "        'id': 1,\n" +
                "        'created': 2,\n" +
                "        'name': 'name',\n" +
                "        'screenName': 'screenName'\n" +
                "    },\n" +
                "    'messages': [{\n" +
                "            'id': 11,\n" +
                "            'timestamp': 12,\n" +
                "            'text': 'message text',\n" +
                "            'author': {\n" +
                "                'id': 1,\n" +
                "                'created': 2,\n" +
                "                'name': 'name',\n" +
                "                'screenName': 'screenName'\n" +
                "            }\n" +
                "        }, {\n" +
                "            'id': 21,\n" +
                "            'timestamp': 22,\n" +
                "            'text': 'message text',\n" +
                "            'author': {\n" +
                "                'id': 1,\n" +
                "                'created': 2,\n" +
                "                'name': 'name',\n" +
                "                'screenName': 'screenName'\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        System.out.println(redirectedOut.toString());
        JSONAssert.assertEquals(expected, redirectedOut.toString(), false);
    }
}