package com.dude.dms.backend.data.entity;

import java.util.List;

@FunctionalInterface
public interface Historical<T extends History> {

    List<T> getHistory();
}
