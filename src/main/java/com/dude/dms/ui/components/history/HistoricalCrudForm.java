package com.dude.dms.ui.components.history;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.data.Diffable;
import com.dude.dms.backend.data.Historical;
import com.dude.dms.backend.data.history.History;
import com.dude.dms.backend.service.HistoricalCrudService;
import com.dude.dms.backend.service.HistoryCrudService;
import com.dude.dms.ui.events.CrudFormCreateListener;
import com.dude.dms.ui.events.CrudFormErrorListener;
import com.dude.dms.ui.events.CrudFormSaveListener;
import com.github.appreciated.IronCollapse;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.ValueProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class HistoricalCrudForm<T extends DataEntity & Historical<U> & Diffable<T>, U extends History> extends FormLayout {

    private Binder<T> binder;

    private final Class<T> clazz;

    private CrudFormCreateListener<T> createListener;
    private CrudFormSaveListener<T> saveListener;
    private CrudFormErrorListener errorListener;

    private final HistoricalCrudService<T, U> service;
    private final HistoryCrudService<T, U> historyService;

    private HistoryView<T, U> historyView;

    private final Button saveButton;
    private boolean canUpdate = true;
    private boolean canCreate = true;

    public HistoricalCrudForm(Class<T> clazz, HistoricalCrudService<T, U> service, HistoryCrudService<T, U> historyService) {
        this.clazz = clazz;
        this.service = service;
        this.historyService = historyService;

        binder = new Binder<>();

        saveButton = new Button("Create", e -> {
            if (binder.getBean() != null) {
                update();
            } else {
                create();
            }
        });
        saveButton.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public void load(T entity) {
        binder.setBean(entity);
        historyView.load(entity);
        saveButton.setText("Save");
        checkPermissions();
    }

    public void reload() {
        historyView.reload();
    }

    public <R> void addFormField(String label, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter) {
        addFormItem((Component) component, label);
        ((HasElement) component).getElement().getStyle().set("boxSizing", "borderBox").set("width", "100%").set("minWidth", "0");
        binder.forField(component).bind(getter, setter);
    }

    public <R> void addFormField(String label, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter, SerializablePredicate<? super R> validator, String errorMessage) {
        addFormItem((Component) component, label);
        ((HasElement) component).getElement().getStyle().set("boxSizing", "borderBox").set("width", "100%").set("minWidth", "0");
        binder.forField(component).withValidator(validator, errorMessage).bind(getter, setter);
    }

    private void create() {
        if (canCreate) {
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
                        createListener.onCreate(instance);
                    }
                }
            });
        }
    }

    protected void update() {
        if (canUpdate) {
            BinderValidationStatus<T> status = binder.validate();
            if (status.hasErrors()) {
                if (errorListener != null) {
                    errorListener.onError(status.getValidationErrors());
                }
            } else {
                T bean = binder.getBean();
                service.save(bean);
                if (saveListener != null) {
                    saveListener.onSave(bean);
                }
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

    public void clear() {
        removeAll();
        binder = new Binder<>();
        if (historyView != null) {
            historyView.clear();
        }
        saveButton.setText("Create");
        checkPermissions();
    }

    public void setCreateListener(CrudFormCreateListener<T> createListener) {
        this.createListener = createListener;
    }

    public void setSaveListener(CrudFormSaveListener<T> saveListener) {
        this.saveListener = saveListener;
    }

    public void setErrorListener(CrudFormErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setPermissions(boolean canUpdate, boolean canCreate) {
        if (canUpdate || canCreate) {
            add(saveButton);
        }
        this.canUpdate = canUpdate;
        this.canCreate = canCreate;
        checkPermissions();
    }

    private void checkPermissions() {
        saveButton.setEnabled((binder.getBean() != null && canUpdate) || (binder.getBean() == null && canCreate));
    }

    public void addHistory() {
        historyView = new HistoryView<>(historyService);
        historyView.getElement().getStyle().set("padding", "10px");
        IronCollapse collapse = new IronCollapse(historyView);
        collapse.setHeightFull();
        collapse.setOpened(true);
        collapse.setHeight("20rem");
        collapse.getElement().getStyle().set("paddingTop", "20px");
        Button history = new Button("History", e -> {
            collapse.toggle();
            if (collapse.isVisible()) {
                historyView.reload();
            }
        });
        history.setWidthFull();
        Div div = new Div(history, collapse);
        div.getElement().getStyle().set("border", "1px solid var(--lumo-primary-color)");
        add(div);
    }
}