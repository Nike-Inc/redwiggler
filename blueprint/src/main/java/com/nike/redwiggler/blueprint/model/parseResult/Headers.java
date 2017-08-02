package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class Headers {
    List<HeaderContent> content;
    String element;

    public List<HttpHeader> asHttpHeaders() {
        return content.stream()
                .map(HeaderContent::getContent)
                .map(ContentContent::getContent)
                .map(x -> x.right().value().left().value())
                .map(x -> HttpHeader.builder()
                        .key(x.getKey().getContent())
                        .value(x.getValue().getContent())
                        .build())
                .collect(Collectors.toList());
    }
}
