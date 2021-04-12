package ru.ifmo.software_design.akka.actor.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SearchRequest {
    String query;
}
