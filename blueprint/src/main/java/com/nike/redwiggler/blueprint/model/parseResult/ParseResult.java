package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class ParseResult {
    String element;
    List<Content> content;

    public List<ResourceGroup> toResourceGroups() {
        if (content.size() == 1) {
            return content.get(0).getContent().getContent().left().value().stream()
                    .map(Content::convertElement)
                    .map(x -> (ResourceGroup) x)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalStateException("Don't know how to handle multiple content sections");
        }
    }
}
