package com.aazayka.entities;

import com.aazayka.services.DateConverter;
import lombok.Value;

@Value
public class Author {
    private static DateConverter dateConverter = new DateConverter();
    long id;
    String createdAt;
    String name;
    String screenName;

    public long getCreated() {
        return dateConverter.convertToEpochSeconds(createdAt);
    }
}
