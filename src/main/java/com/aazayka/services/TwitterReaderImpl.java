package com.aazayka.services;

import com.aazayka.exceptions.TwitterReadException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.interview.oauth.twitter.TwitterAuthenticationException;
import org.interview.oauth.twitter.TwitterAuthenticator;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class TwitterReaderImpl implements TwitterReader {

    private static final String CONSUMER_KEY = "RLSrphihyR4G2UxvA0XBkLAdl";
    private static final String CONSUMER_SECRET = "FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4";
    private static final String BIBER_URL = "https://stream.twitter.com/1.1/statuses/filter.json?track=bieber";

    public InputStream getContent() throws TwitterAuthenticationException, TwitterReadException, IOException {
        TwitterAuthenticator twitterAuthenticator = new TwitterAuthenticator(System.out, CONSUMER_KEY, CONSUMER_SECRET);
        HttpRequestFactory httpRequestFactory = twitterAuthenticator.getAuthorizedHttpRequestFactory();
        //TODO: check
        HttpRequest request = httpRequestFactory.buildRequest("POST", new GenericUrl(BIBER_URL), null);

        HttpResponse response = request.execute();

        if (!response.isSuccessStatusCode()) {
            log.error("Invalid response code " + response.getStatusCode());
            throw new TwitterReadException();
        }

        return response.getContent();
    }
}
