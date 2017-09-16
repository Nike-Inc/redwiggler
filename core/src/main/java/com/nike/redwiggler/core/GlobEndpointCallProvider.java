package com.nike.redwiggler.core;

import com.nike.redwiggler.core.models.EndpointCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GlobEndpointCallProvider implements EndpointCallProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobEndpointCallProvider.class);
    private final File dir;
    private final String pattern;

    public GlobEndpointCallProvider(File dir, String pattern) {
        this.dir = dir;
        this.pattern = pattern;
    }

    @Override
    public List<EndpointCall> getCalls() {
        FileBasedEndpointCallProvider callProvider = new FileBasedEndpointCallProvider(findFiles());
        return callProvider.getCalls();
    }

    private List<File> findFiles() {
        if (!dir.exists()) {
            LOGGER.error("The following directory does not exist: {}", dir.getPath());
            throw new IllegalArgumentException("Cannot load data from nonexistant directory.");
        } else {
            File[] files = dir.listFiles();
            return Arrays.stream(files)
                    .filter(fileEntry -> !fileEntry.isDirectory() && Pattern.matches(pattern, fileEntry.getName()))
                    .collect(Collectors.toList());
        }
    }
}
