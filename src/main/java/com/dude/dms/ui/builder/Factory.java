package com.dude.dms.ui.builder;

public abstract class Factory {

    protected final BuilderFactory builderFactory;

    protected Factory(BuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }
}