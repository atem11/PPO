package ru.ifmo.software_design.akka.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedAbstractActor;
import akka.routing.SmallestMailboxPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.software_design.akka.actor.data.SearchChildRequest;
import ru.ifmo.software_design.akka.actor.data.SearchItem;
import ru.ifmo.software_design.akka.actor.data.SearchItemStream;
import ru.ifmo.software_design.akka.actor.data.SearchRequest;
import ru.ifmo.software_design.search.SearchStub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MasterSearchActor extends UntypedAbstractActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterSearchActor.class);

    private final ActorRef router;
    private final CompletableFuture<Stream<SearchItem>> response;
    private final List<SearchItem> results;
    private int responsesCount;

    public MasterSearchActor(CompletableFuture<Stream<SearchItem>> response) {
        this.response = response;
        this.router = getContext().actorOf(new SmallestMailboxPool(SearchStub.SearchersType.values().length)
                        .props(Props.create(ChildSearchActor.class)),
                "router");
        this.results = new ArrayList<>();
        this.responsesCount = 0;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof SearchRequest) {
            SearchRequest request = (SearchRequest) message;
            Arrays.stream(SearchStub.SearchersType.values()).forEach(type -> router.tell(new SearchChildRequest(type, request.getQuery()), self()));
        } else if (message instanceof SearchItemStream) {
            SearchItemStream request = (SearchItemStream) message;
            results.addAll(request.getValues().collect(Collectors.toList()));
            onComplete();
        } else if (message instanceof ReceiveTimeout) {
            response.complete(results.stream());
            getContext().cancelReceiveTimeout();
            context().stop(self());
        } else {
            LOGGER.info("Unknown message: " + message);
        }

    }

    private void onComplete() {
        responsesCount += 1;
        if (responsesCount == SearchStub.SearchersType.values().length) {
            response.complete(results.stream());
            getContext().cancelReceiveTimeout();
            context().stop(self());
        }
    }
}
