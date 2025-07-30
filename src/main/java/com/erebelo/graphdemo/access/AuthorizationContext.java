package com.erebelo.graphdemo.access;

import com.erebelo.graphdemo.common.version.Locator;

public interface AuthorizationContext {

    String principal();

    Locator target();

    Operation operation();
}
