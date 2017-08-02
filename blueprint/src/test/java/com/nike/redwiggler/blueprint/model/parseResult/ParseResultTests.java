package com.nike.redwiggler.blueprint.model.parseResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ParseResultTests {

    @Test
    public void basic() throws IOException {
        doTest("basic", singletonList(
                ResourceGroup.builder()
                        .resources(singletonList(
                                Resource.builder()
                                        .uriTemplate("/foo")
                                        .httpTransactions(Collections.emptyList())
                                        .build()
                        ))
                        .build()
        ));
    }

    @Test
    public void helloWorld() throws IOException {
        doTest("helloWorld", singletonList(
                ResourceGroup.builder()
                        .resources(singletonList(
                                Resource.builder()
                                        .uriTemplate("/message")
                                        .httpTransactions(singletonList(
                                                HttpTransaction.builder()
                                                        .requests(singletonList(HttpRequest.builder()
                                                                .method("GET")
                                                                .headers(emptyList())
                                                                .build()
                                                        ))
                                                        .responses(singletonList(
                                                                HttpResponse.builder()
                                                                        .statusCode(200)
                                                                        .headers(Collections.emptyList())
                                                                        .build()
                                                        ))
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build()
        ));
    }

    @Test
    public void postJson() throws IOException {
        doTest("postJson", singletonList(
                ResourceGroup.builder()
                        .resources(singletonList(
                                Resource.builder()
                                        .uriTemplate("/helloworld")
                                        .httpTransactions(Collections.singletonList(
                                                HttpTransaction.builder()
                                                        .requests(singletonList(HttpRequest.builder()
                                                                .method("POST")
                                                                .headers(singletonList(
                                                                        HttpHeader.builder()
                                                                                .key("Authorization")
                                                                                .value("MyAuthToken")
                                                                        .build()
                                                                ))
                                                                .build()
                                                        ))
                                                        .responses(singletonList(HttpResponse.builder()
                                                                .statusCode(201)
                                                                .headers(emptyList())
                                                                .build()
                                                        ))
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build()
        ));
    }

    @Test
    public void getAndPost() throws IOException {
        doTest("getAndPost", singletonList(
                ResourceGroup.builder()
                        .resources(singletonList(
                                Resource.builder()
                                        .uriTemplate("/message")
                                        .httpTransactions(Collections.singletonList(
                                                HttpTransaction.builder()
                                                        .requests(singletonList(HttpRequest.builder()
                                                                .method("GET")
                                                                .headers(emptyList())
                                                                .build()
                                                        ))
                                                        .responses(singletonList(HttpResponse.builder()
                                                                .statusCode(200)
                                                                .headers(emptyList())
                                                                .build()
                                                        ))
                                                .build()
                                        ))
                                        .build()
                        ))
                        .build()
        ));
    }

    @Test
    public void duplicateEndpoit() throws IOException {
        doTest("duplicateEndpoint", null);
    }

    private void doTest(String name, List<ResourceGroup> expected) throws IOException {
        InputStream is = getClass().getResourceAsStream(name + "/parsedResult.json");
        ObjectMapper objectMapper = new ObjectMapper();
        ParseResult parseResult = objectMapper.readValue(is, ParseResult.class);
        System.out.println(parseResult.toResourceGroups());
        Assertions.assertThat(parseResult.toResourceGroups()).isEqualTo(expected);
    }
}
