package com.nike.redwiggler.core;

import com.nike.redwiggler.core.models.EndpointCall;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;


public class FileBasedEndpointCallProvider implements EndpointCallProvider {
    private final List<File> files;

    public FileBasedEndpointCallProvider(List<File> files) {
        this.files = files;
    }

    @Override
    public List<EndpointCall> getCalls() {
        return files
                .stream()
                .map(EndpointCall::fromFile)
                .collect(Collectors.toList());
    }

}
