package com.aazayka.services;

import com.aazayka.entities.Author;
import com.aazayka.entities.Message;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

public interface ResultPrinter {

    void setWriter(PrintStream writer);

    void print(Map<Author, Collection<Message>> messages);
}
