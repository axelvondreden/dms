package com.dude.dms.ui.events;

@FunctionalInterface
public interface CrudFormCreateListener<T> {

    void onCreate(T entity);

}
