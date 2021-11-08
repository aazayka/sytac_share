package com.aazayka.services;

import com.aazayka.exceptions.TwitterReadException;
import org.interview.oauth.twitter.TwitterAuthenticationException;

import java.io.IOException;
import java.io.InputStream;

public interface TwitterReader {
    InputStream getContent() throws TwitterAuthenticationException, TwitterReadException, IOException;
}
