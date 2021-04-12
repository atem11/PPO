package ru.ifmo.software_design;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.software_design.akka.actor.MasterSearchActor;
import ru.ifmo.software_design.akka.actor.data.SearchItem;
import ru.ifmo.software_design.akka.actor.data.SearchRequest;
import ru.ifmo.software_design.search.SearchStub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class SearchActorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchActorTest.class);

    private ActorSystem system;
    private SearchStub searchStub;

    @Before
    public void init() {
        system = ActorSystem.create("TestAkkaSearcher");
        searchStub = SearchStub.getInstance();
    }

    @After
    public void clear() {
        system.terminate();
        searchStub.updateSleepSeconds(0);
        system = null;
        searchStub = null;
    }

    @Test
    public void oneRequestTest() {
        CompletableFuture<Stream<SearchItem>> future = new CompletableFuture<>();
        ActorRef master = system.actorOf(
                Props.create(MasterSearchActor.class, future),
                "TestMaster");
        master.tell(new SearchRequest("test1"), ActorRef.noSender());
        Stream<SearchItem> response = null;
        try {
            response = future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(checkResponse(response));
    }

    @Test
    public void fewRequestTest() {
        CompletableFuture<Stream<SearchItem>> future = new CompletableFuture<>();
        ActorRef master = system.actorOf(
                Props.create(MasterSearchActor.class, future),
                "TestMaster1");
        master.tell(new SearchRequest("test1"), ActorRef.noSender());
        Stream<SearchItem> response = null;
        try {
            response = future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(checkResponse(response));

        future = new CompletableFuture<>();
        master = system.actorOf(
                Props.create(MasterSearchActor.class, future),
                "TestMaster2");
        master.tell(new SearchRequest("test2"), ActorRef.noSender());
        response = null;
        try {
            response = future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(checkResponse(response));
    }

    @Test
    public void timeoutTest() {
        CompletableFuture<Stream<SearchItem>> future = new CompletableFuture<>();
        ActorRef master = system.actorOf(
                Props.create(MasterSearchActor.class, future),
                "TestMaster");
        searchStub.updateSleepSeconds(10);
        master.tell(new SearchRequest("test1"), ActorRef.noSender());
        Stream<SearchItem> response = null;
        try {
            response = future.get(5, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
        Assert.assertNull(response);
    }

    private boolean checkResponse(Stream<SearchItem> response) {
        if (response == null) {
            return false;
        }
        Map<SearchStub.SearchersType, Integer> results = new HashMap<>();
        for (SearchStub.SearchersType type : SearchStub.SearchersType.values()) {
            results.put(type, 0);
        }
        response.forEach(item -> results.computeIfPresent(item.getSource(), ((type, integer) -> integer + 1)));
        AtomicBoolean res = new AtomicBoolean(true);
        results.forEach(((type, cnt) -> {
            if (cnt != SearchStub.SEARCH_SIZE) {
                res.set(false);
            }
        }));
        return res.get();
    }
}