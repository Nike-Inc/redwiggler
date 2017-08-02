package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Value;

@Value
public class Attributes {
    Headers headers;
    String method;
    Object meta;
    String href;
    String contentType;
    Integer statusCode;
    Object hrefVariables;
    Object code;
    Object sourceMap;
}
