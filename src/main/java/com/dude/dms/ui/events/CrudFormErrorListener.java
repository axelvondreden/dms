package com.dude.dms.ui.events;

import com.vaadin.flow.data.binder.ValidationResult;

import java.util.List;

@FunctionalInterface
public interface CrudFormErrorListener {

    void onError(List<ValidationResult> errors);

}
