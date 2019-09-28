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
    }

    protected <R> void addFormField(String label, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter) {
        ((HasStyle) component).addClassName("full-width");
        addFormItem(new FormItem((Component) component), label);
        binder.bind(component, getter, setter);
    }

    public Binder<T> getBinder() {
        return binder;
    }
}
