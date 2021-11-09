package com.aazayka;

import com.aazayka.entities.Author;
import com.aazayka.services.DateConverter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TestData {
    private final JsonObject jsonMessage;
    private static final String AUTHOR_CREATED_STR = "Tue Aug 21 16:27:51 +0000 1900";
    private static final long AUTHOR_ID = 0L;
    private static final String AUTHOR_NAME = "Sample author";
    private static final long MESSAGE_TS = 0L;
    private static final long MESSAGE_ID = 0L;
    private static final String MESSAGE_TEXT = "Sample text";

    private static final String SAMPLE_MESSAGE = "{\n" +
            "    \"created_at\": \"Mon Nov 08 23:48:20 +0000 2021\",\n" +
            "    \"id\": " + MESSAGE_ID + ",\n" +
            "    \"id_str\": \"" + MESSAGE_ID + "\",\n" +
            "    \"text\": \"" + MESSAGE_TEXT + "\",\n" +
            "    \"source\": \"\\u003ca href=\\\"http:\\/\\/twitter.com\\/download\\/android\\\" rel=\\\"nofollow\\\"\\u003eTwitter for Android\\u003c\\/a\\u003e\",\n" +
            "    \"truncated\": true,\n" +
            "    \"in_reply_to_status_id\": null,\n" +
            "    \"in_reply_to_status_id_str\": null,\n" +
            "    \"in_reply_to_user_id\": null,\n" +
            "    \"in_reply_to_user_id_str\": null,\n" +
            "    \"in_reply_to_screen_name\": null,\n" +
            "    \"user\": {\n" +
            "        \"id\": " + AUTHOR_ID + ",\n" +
            "        \"id_str\": \"" + AUTHOR_ID + "\",\n" +
            "        \"name\": \"" + AUTHOR_NAME + "\",\n" +
            "        \"screen_name\": \"" + AUTHOR_NAME + "\",\n" +
            "        \"location\": \"Paris\",\n" +
            "        \"url\": null,\n" +
            "        \"description\": \"\\ud83c\\udf0cwe fall into the rabbit hole\\ud83c\\udf0c\",\n" +
            "        \"translator_type\": \"none\",\n" +
            "        \"protected\": false,\n" +
            "        \"verified\": false,\n" +
            "        \"followers_count\": 615,\n" +
            "        \"friends_count\": 717,\n" +
            "        \"listed_count\": 3,\n" +
            "        \"favourites_count\": 4804,\n" +
            "        \"statuses_count\": 7914,\n" +
            "        \"created_at\": \"" + AUTHOR_CREATED_STR + "\",\n" +
            "        \"utc_offset\": null,\n" +
            "        \"time_zone\": null,\n" +
            "        \"geo_enabled\": true,\n" +
            "        \"lang\": null,\n" +
            "        \"contributors_enabled\": false,\n" +
            "        \"is_translator\": false,\n" +
            "        \"profile_background_color\": \"F5F8FA\",\n" +
            "        \"profile_background_image_url\": \"\",\n" +
            "        \"profile_background_image_url_https\": \"\",\n" +
            "        \"profile_background_tile\": false,\n" +
            "        \"profile_link_color\": \"1DA1F2\",\n" +
            "        \"profile_sidebar_border_color\": \"C0DEED\",\n" +
            "        \"profile_sidebar_fill_color\": \"DDEEF6\",\n" +
            "        \"profile_text_color\": \"333333\",\n" +
            "        \"profile_use_background_image\": true,\n" +
            "        \"profile_image_url\": \"http:\\/\\/pbs.twimg.com\\/profile_images\\/1395842182498631684\\/LCXCyXGI_normal.jpg\",\n" +
            "        \"profile_image_url_https\": \"https:\\/\\/pbs.twimg.com\\/profile_images\\/1395842182498631684\\/LCXCyXGI_normal.jpg\",\n" +
            "        \"profile_banner_url\": \"https:\\/\\/pbs.twimg.com\\/profile_banners\\/899669385350193153\\/1621629685\",\n" +
            "        \"default_profile\": true,\n" +
            "        \"default_profile_image\": false,\n" +
            "        \"following\": null,\n" +
            "        \"follow_request_sent\": null,\n" +
            "        \"notifications\": null,\n" +
            "        \"withheld_in_countries\": []\n" +
            "    },\n" +
            "    \"geo\": null,\n" +
            "    \"coordinates\": null,\n" +
            "    \"place\": null,\n" +
            "    \"contributors\": null,\n" +
            "    \"is_quote_status\": false,\n" +
            "    \"extended_tweet\": {\n" +
            "        \"full_text\": \"As pessoas que falam que o bieber n\\u00e3o tem relev\\u00e2ncia s\\u00e3o as mesmas que falam que a Hailey s\\u00f3 tem uma carreira por causa do Bieber, vai saher...\",\n" +
            "        \"display_text_range\": [0, 143],\n" +
            "        \"entities\": {\n" +
            "            \"hashtags\": [],\n" +
            "            \"urls\": [],\n" +
            "            \"user_mentions\": [],\n" +
            "            \"symbols\": []\n" +
            "        }\n" +
            "    },\n" +
            "    \"quote_count\": 0,\n" +
            "    \"reply_count\": 0,\n" +
            "    \"retweet_count\": 0,\n" +
            "    \"favorite_count\": 0,\n" +
            "    \"entities\": {\n" +
            "        \"hashtags\": [],\n" +
            "        \"urls\": [{\n" +
            "                \"url\": \"https:\\/\\/t.co\\/uUvsZ6dTdU\",\n" +
            "                \"expanded_url\": \"https:\\/\\/twitter.com\\/i\\/web\\/status\\/1457857549550723077\",\n" +
            "                \"display_url\": \"twitter.com\\/i\\/web\\/status\\/1\\u2026\",\n" +
            "                \"indices\": [117, 140]\n" +
            "            }\n" +
            "        ],\n" +
            "        \"user_mentions\": [],\n" +
            "        \"symbols\": []\n" +
            "    },\n" +
            "    \"favorited\": false,\n" +
            "    \"retweeted\": false,\n" +
            "    \"filter_level\": \"low\",\n" +
            "    \"lang\": \"pt\",\n" +
            "    \"timestamp_ms\": \"" + MESSAGE_TS + "\"\n" +
            "}";

    public TestData() {
        Gson gson = new Gson();
        jsonMessage = gson.fromJson(SAMPLE_MESSAGE, JsonObject.class);
    }

    public TestData withAuthor(Author author) {
        JsonObject authorJson = jsonMessage.get("user").getAsJsonObject();
        authorJson.addProperty("id", author.getId());
        authorJson.addProperty("id_str", Long.toString(author.getId()));
        authorJson.addProperty("name", author.getName());
        authorJson.addProperty("screen_name", author.getScreenName());
        authorJson.addProperty("created_at",
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(author.getCreated()), ZoneOffset.UTC)
                        .format(DateConverter.DATE_FORMATTER));
        return this;
    }

    public TestData withMessageTs(long ts) {
        jsonMessage.addProperty("timestamp_ms", ts * 1000);
        return this;
    }

    public TestData withMessageId(long id) {
        jsonMessage.addProperty("id", id);
        jsonMessage.addProperty("id_str", Long.toString(id));
        return this;
    }

    public TestData withMessageText(String text) {
        jsonMessage.addProperty("text", text);
        return this;
    }

    public String getMessage(){
        return jsonMessage.toString() + "\n";
    }
}
