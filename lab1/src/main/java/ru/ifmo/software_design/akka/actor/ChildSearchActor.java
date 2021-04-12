package ru.ifmo.software_design.akka.actor;

import akka.actor.UntypedAbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.software_design.akka.actor.data.SearchChildRequest;
import ru.ifmo.software_design.akka.actor.data.SearchItemStream;
import ru.ifmo.software_design.search.SearchStub;

public class ChildSearchActor extends UntypedAbstractActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChildSearchActor.class);

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof SearchChildRequest) {
            SearchChildRequest request = (SearchChildRequest) message;
            SearchStub stub = SearchStub.getInstance();
            switch (request.getSource()) {
                case GOOGLE:
                case YANDEX:
                case BING:
                    sender().tell(new SearchItemStream(stub.search(request.getQuery(), request.getSource())), self());
                    break;
                default:
                    LOGGER.info("Unknown search type: " + request.getSource());
                    throw new IllegalArgumentException("Unknown search type: " + request.getSource());
            }
        } else {
            LOGGER.info("Unknown message: " + message);
        }
        getContext().stop(self());
    }
}
