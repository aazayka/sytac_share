package com.aazayka.services;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;

import java.util.Collection;

public interface ResultPrinter {
    public void print(Author author, Collection<Message> messages);
}
