package ru.ifmo.software_design.akka.actor.data;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import ru.ifmo.software_design.search.SearchStub;

@Value
@ToString
@AllArgsConstructor
public class SearchItem {
    SearchStub.SearchersType source;
    String title;
    String snippet;
}
