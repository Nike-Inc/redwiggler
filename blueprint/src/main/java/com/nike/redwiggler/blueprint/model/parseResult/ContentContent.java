package com.nike.redwiggler.blueprint.model.parseResult;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fj.data.Either;
import lombok.Builder;
import lombok.Value;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Value
@Builder
public class ContentContent {

    Either<List<Content>, Either<Content, String>> content;

    public static class ContentDeserializer extends JsonDeserializer<ContentContent> {
        @Override
        public ContentContent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (p.getCurrentToken() == JsonToken.START_ARRAY) {
                List<Content> content = p.readValueAs(new TypeReference<List<Content>>() {
                });
                return ContentContent.builder()
                        .content(Either.left(content))
                        .build();
            } else if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
                //String text = p.nextTextValue();
                //JsonToken token = p.nextToken();
                return ContentContent.builder()
                        .content(Either.right(Either.right(p.getValueAsString())))
                        .build();
            } else if (p.getCurrentToken() == JsonToken.START_OBJECT) {
                return ContentContent.builder()
                        .content(Either.right(Either.left(p.readValueAs(Content.class))))
                        .build();
            } else {
                throw new IllegalStateException("Unknown json token: " + p.getCurrentToken() + " -> " + p.getCurrentLocation());
            }
        }
    }
}
