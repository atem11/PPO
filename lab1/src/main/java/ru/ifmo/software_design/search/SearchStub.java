package ru.ifmo.software_design.search;

import ru.ifmo.software_design.akka.actor.data.SearchItem;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearchStub {
    private static SearchStub instance;
    public static final int SEARCH_SIZE = 5;
    private static final String CHAR_LIST =
            "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-.!?";

    private int sleepSeconds = 0;

    private SearchStub() {
    }

    public synchronized static SearchStub getInstance() {
        if (instance == null) {
            instance = new SearchStub();
        }
        return instance;
    }


    public Stream<SearchItem> search(String query, SearchersType type) {
        if (sleepSeconds > 0) {
            try {
                Thread.sleep(sleepSeconds * 1000);
            } catch (InterruptedException ignored) {
            }
        }
        List<SearchItem> res = new ArrayList<>();
        for (int i = 0; i < SEARCH_SIZE; i++) {
            res.add(new SearchItem(type, generateRandomString(7), generateRandomString(25)));
        }
        return res.stream();
    }

    public void updateSleepSeconds(int seconds) {
        assert (seconds >= 0);
        this.sleepSeconds = seconds;
    }

    private String generateRandomString(int length) {
        StringBuilder randStr = new StringBuilder(length);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            randStr.append(CHAR_LIST.charAt(secureRandom.nextInt(CHAR_LIST.length())));
        }
        return randStr.toString();
    }

    public enum SearchersType {
        GOOGLE("google.com"),
        YANDEX("yandex.ru"),
        BING("bing.com"),
        ;

        private final String name;

        SearchersType(String s) {
            this.name = s;
        }

        public String toString() {
            return name;
        }
    }
}
