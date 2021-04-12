package ru.ifmo.software_design.akka.actor.data;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.stream.Stream;

@AllArgsConstructor
@Value
public class SearchItemStream {
    Stream<SearchItem> values;
}
