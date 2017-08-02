package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.NonNull;
import lombok.Value;

@Value
public class ModelContent {
    @NonNull
    String element, content;
}
