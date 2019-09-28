package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import com.dude.dms.backend.service.CrudService;
import com.dude.dms.ui.views.HasNotifications;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

public abstract class HistoricalCrudView<T extends DataEntity & Historical<U>, U extends History> extends SplitLayout implements AfterNavigationObserver, HasNotifications {

    protected Grid<T> grid;

    private final HistoricalCrudForm<T, U> crudForm;

    protected final CrudService<T> service;

    protected abstract void defineProperties();

    protected HistoricalCrudView(Class<T> clazz, CrudService<T> service) {
        this.service = service;

        setSizeFull();
        setOrientation(Orientation.HORIZONTAL);

        grid = new Grid<>();
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> fillForm(event.getValue()));
        addToPrimary(grid);

        crudForm = new HistoricalCrudForm<>(clazz, service);
        crudForm.setCreateListener(() -> {
            fillGrid();
            clearForm();
            showNotification("Created!");
        });
        crudForm.setSaveListener(() -> showNotification("Saved!"));
        crudForm.setErrorListener(errors -> errors.forEach(e -> showNotification(e.getErrorMessage(), true)));
        addToSecondary(crudForm);
    }

    private void fillGrid() {
        grid.setItems(service.findAll());
    }

    private void fillForm(T entity) {
        if (entity != null) {
            crudForm.getBinder().setBean(entity);
        }
    }

    private void clearForm() {
        crudForm.getBinder().setBean(null);
        crudForm.getBinder().readBean(null);
    }

    protected <R> void addProperty(String name, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter) {
        grid.addColumn(getter).setHeader(name);
        crudForm.addFormField(name, component, getter, setter);
    }

    protected <R> void addProperty(String name, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter, SerializablePredicate<? super R> validator, String errorMessage) {
        grid.addColumn(getter).setHeader(name);
        crudForm.addFormField(name, component, getter, setter, validator, errorMessage);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.removeAllColumns();
        fillGrid();
        crudForm.clear();
        defineProperties();
        crudForm.addButtons();
    }

}
