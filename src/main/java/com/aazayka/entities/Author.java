package com.aazayka.entities;

import lombok.Value;

@Value
public class Author {
    long id;
    long created;
    String name;
    String screenName;
}
