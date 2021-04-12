package ru.ifmo.software_design.akka.actor.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ifmo.software_design.search.SearchStub;

@AllArgsConstructor
@Data
public class SearchChildRequest {
    SearchStub.SearchersType source;
    String query;
}
