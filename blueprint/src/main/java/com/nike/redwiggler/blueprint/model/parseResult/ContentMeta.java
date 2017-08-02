package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Value;

import java.util.List;

@Value
public class ContentMeta {
    List<String> classes;
    String title;
}
