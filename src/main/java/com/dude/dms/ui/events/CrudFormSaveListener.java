package com.dude.dms.ui.events;

@FunctionalInterface
public interface CrudFormSaveListener<T> {

    void onSave(T entity);
}