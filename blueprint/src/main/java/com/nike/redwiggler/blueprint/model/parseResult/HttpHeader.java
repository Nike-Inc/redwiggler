package com.nike.redwiggler.blueprint.model.parseResult;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HttpHeader {
    Object key, value;
}
