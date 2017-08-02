package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Resource {
    String uriTemplate;
    List<HttpTransaction> httpTransactions;
}
