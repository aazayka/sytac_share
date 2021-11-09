package com.aazayka.services;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;

import java.util.Collection;
import java.util.Map;

public interface ResultPrinter {
    void print(Map<Author, Collection<Message>> messages);
}
