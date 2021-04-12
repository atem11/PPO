package ru.ifmo.software_design;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.software_design.akka.actor.MasterSearchActor;
import ru.ifmo.software_design.akka.actor.data.SearchItem;
import ru.ifmo.software_design.akka.actor.data.SearchRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("SDAkkaSearcher");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        String in;
        int requests = 0;
        while ((in = bufferedReader.readLine()) != null) {

            if (in.equals("exit")) {
                system.terminate();
                System.exit(0);
            }
            CompletableFuture<Stream<SearchItem>> future = new CompletableFuture<>();
            ActorRef master = system.actorOf(
                    Props.create(MasterSearchActor.class, future),
                    "master" + requests++);
            master.tell(new SearchRequest(in), ActorRef.noSender());
            try {
                Stream<SearchItem> response = future.get();
                response.forEach(System.out::println);
            } catch (Exception e) {
                LOGGER.error("Error during searching results");
            }
        }

    }
}
