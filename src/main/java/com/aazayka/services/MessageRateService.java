package com.aazayka.services;

public interface MessageRateService {
    void start();
    void finish();
    void incrementMessageCounter();
}
