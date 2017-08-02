package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class HttpTransaction {

    @NonNull
    List<HttpRequest> requests;
    @NonNull
    List<HttpResponse> responses;

}
