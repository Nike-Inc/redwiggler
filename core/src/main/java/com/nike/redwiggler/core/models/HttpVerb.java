package com.nike.redwiggler.core.models;

public enum HttpVerb {
    POST,
    GET,
    PUT,
    DELETE,
    OPTION,
    PATCH;

    public static HttpVerb from(String name) {
        return valueOf(name);
    }
}
