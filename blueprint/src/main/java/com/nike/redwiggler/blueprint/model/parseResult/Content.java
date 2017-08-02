package com.nike.redwiggler.blueprint.model.parseResult;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Value
public class Content {
    @JsonDeserialize(using = ContentContent.ContentDeserializer.class)
    ContentContent content;
    String element;
    Attributes attributes;
    ContentMeta meta;
    ModelContent key;
    ModelContent value;

    <T> T convertElement() {
        switch (element) {
            case "category":
                if (meta.getClasses().contains("resourceGroup")) {
                    @SuppressWarnings("unchecked")
                    T t = (T) toResourceGroup();
                    return t;
                } else {
                    throw new IllegalStateException("Unknown category type: " + meta);
                }
            case "resourceGroup": toResourceGroup();
            default: throw new IllegalStateException("Unsupported element type: " + element);
        }
    }

    private ResourceGroup toResourceGroup() {
        return ResourceGroup.builder()
                .resources(content.getContent().left().value().stream()
                        .filter(element("resource"))
                        .map(Content::toResource)
                        .collect(Collectors.toList())
                )
                .build();
    }

    private Predicate<Content> element(String expected) {
        return content -> content.getElement().equals(expected);
    }

    private Resource toResource() {
        return Resource.builder()
                .uriTemplate(attributes.getHref())
                .httpTransactions(content.getContent().left().value().stream()
                .filter(element("transition"))
                .map(Content::toTransition)
                .map(Transition::getHttpTransactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                )
                .build();
    }

    HttpRequest toHttpRequest() {
        return HttpRequest.builder()
                .method(attributes.getMethod())
                .headers(attributes.getHeaders() == null ? Collections.emptyList() : attributes.getHeaders().asHttpHeaders())
                .build();
    }

    List<HttpHeader> toHttpHeaders() {
                return content.getContent().left().value().stream()
                .filter(element("member"))
                .map(Content::toHttpHeader)
                .collect(Collectors.toList());
    }

    HttpHeader toHttpHeader() {
        return HttpHeader.builder()
                .key(content.getContent().right().value().left().value().getKey())
                .value(content.getContent().right().value().left().value().getValue())
                .build();
    }
    HttpResponse toHttpResponse() {
        return HttpResponse.builder()
                .statusCode(attributes.getStatusCode())
                .headers(content.getContent().left().value().stream()
                        .filter(element("httpHeaders"))
                        .map(Content::toHttpHeaders)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
                )
                .build();
    }

    HttpTransaction toHttpTransaction() {
        return HttpTransaction.builder()
                .requests(content.getContent().left().value().stream()
                        .filter(element("httpRequest"))
                        .map(Content::toHttpRequest)
                        .collect(Collectors.toList())
                )
                .responses(content.getContent().left().value().stream()
                        .filter(element("httpResponse"))
                        .map(Content::toHttpResponse)
                        .collect(Collectors.toList())
                )
                .build();
    }

    Transition toTransition() {
        return Transition.builder()
                .httpTransactions(content.getContent().left().value().stream()
                        .filter(element("httpTransaction"))
                        .map(Content::toHttpTransaction)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
