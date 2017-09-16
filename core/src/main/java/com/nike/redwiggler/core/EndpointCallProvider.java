package com.nike.redwiggler.core;

import com.nike.redwiggler.core.models.EndpointCall;

import java.util.*;

public interface EndpointCallProvider {

    List<EndpointCall> getCalls();
}
