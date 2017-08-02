package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Transition {
    List<HttpTransaction> httpTransactions;
}
