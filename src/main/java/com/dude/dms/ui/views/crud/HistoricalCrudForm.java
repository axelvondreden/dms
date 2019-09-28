package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;

public class HistoricalCrudForm<T extends Historical<U>, U extends History> extends FormLayout {

    private final Binder<T> binder;

    protected HistoricalCrudForm() {
        binder = new Binder<>();
        getElement().getStyle().set("padding", "10px");
    }

    protected <R> void addFormField(String label, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter) {
        addFormItem((Component) component, label);
        ((HasStyle) component).addClassName("full-width");
        binder.bind(component, getter, setter);
    }

    public Binder<T> getBinder() {
        return binder;
    }
}
