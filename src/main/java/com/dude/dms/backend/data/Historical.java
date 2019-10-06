package com.dude.dms.backend.data;

import com.dude.dms.backend.data.history.History;

import java.util.List;

@FunctionalInterface
public interface Historical<T extends History> {

    List<T> getHistory();
}