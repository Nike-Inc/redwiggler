package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class HttpRequest {

    String method;
    @NonNull
    List<HttpHeader> headers;
}
