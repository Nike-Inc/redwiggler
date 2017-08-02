package com.nike.redwiggler.blueprint.model.parseResult;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@lombok.Value
public class HeaderContent {
    Object headerValue;
    Object key;
    String element;
    @JsonDeserialize(using = ContentContent.ContentDeserializer.class)
    ContentContent content;
    Object meta;
    Attributes attributes;
}
