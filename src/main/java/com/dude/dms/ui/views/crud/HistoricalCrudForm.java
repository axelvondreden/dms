package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import com.dude.dms.backend.service.CrudService;
import com.dude.dms.ui.events.CrudFormCreateListener;
import com.dude.dms.ui.events.CrudFormErrorListener;
import com.dude.dms.ui.events.CrudFormSaveListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.ValueProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class HistoricalCrudForm<T extends DataEntity & Historical<U>, U extends History> extends FormLayout {

    private Binder<T> binder;

    private final Class<T> clazz;

    private CrudFormCreateListener createListener;
    private CrudFormSaveListener saveListener;
    private CrudFormErrorListener errorListener;

    private final CrudService<T> service;

    protected HistoricalCrudForm(Class<T> clazz, CrudService<T> service) {
        this.clazz = clazz;
        this.service = service;

        setWidth("300px");

        binder = new Binder<>();
        getElement().getStyle().set("padding", "10px");
    }

    protected <R> void addFormField(String label, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter) {
        addFormItem((Component) component, label);
        ((HasStyle) component).addClassName("full-width");
        binder.forField(component).bind(getter, setter);
    }

    protected <R> void addFormField(String label, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter, SerializablePredicate<? super R> validator, String errorMessage) {
        addFormItem((Component) component, label);
        ((HasStyle) component).addClassName("full-width");
        binder.forField(component).withValidator(validator, errorMessage).bind(getter, setter);
    }

    protected void create() {
        Optional<T> opt = createEmpty();
        opt.ifPresent(instance -> {
            BinderValidationStatus<T> status = binder.validate();
            if (status.hasErrors()) {
                if (errorListener != null) {
                    errorListener.onError(status.getValidationErrors());
                }
            } else {
                binder.writeBeanIfValid(instance);
                service.create(instance);
                if (createListener != null) {
                    createListener.onCreate();
                }
            }
        });
    }

    protected void save() {
        BinderValidationStatus<T> status = binder.validate();
        if (status.hasErrors()) {
            if (errorListener != null) {
                errorListener.onError(status.getValidationErrors());
            }
        } else {
            service.save(binder.getBean());
            if (saveListener != null) {
                saveListener.onSave();
            }
        }
    }

    private Optional<T> createEmpty() {
        try {
            return Optional.of(clazz.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    protected void clear() {
        removeAll();
        binder = new Binder<>();
    }

    public Binder<T> getBinder() {
        return binder;
    }

    public void setCreateListener(CrudFormCreateListener createListener) {
        this.createListener = createListener;
    }

    public void setSaveListener(CrudFormSaveListener saveListener) {
        this.saveListener = saveListener;
    }

    public void addButtons() {
        Button create = new Button("Create", e -> create());
        create.setWidthFull();
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(create);

        Button save = new Button("Save", e -> save());
        save.setWidthFull();
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(save);
    }

    public void setErrorListener(CrudFormErrorListener errorListener) {
        this.errorListener = errorListener;
    }
}
