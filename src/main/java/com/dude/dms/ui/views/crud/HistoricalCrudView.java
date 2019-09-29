package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Diffable;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import com.dude.dms.backend.service.HistoricalCrudService;
import com.dude.dms.backend.service.HistoryCrudService;
import com.dude.dms.ui.views.HasNotifications;
import com.dude.dms.ui.components.CrudForm;
import com.dude.dms.ui.components.HistoryView;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import org.hibernate.Hibernate;

public abstract class HistoricalCrudView<T extends DataEntity & Historical<U> & Diffable<T>, U extends History> extends SplitLayout implements AfterNavigationObserver, HasNotifications {

    protected Grid<T> grid;

    private final CrudForm<T, U> crudForm;

    private final HistoryView<T, U> historyView;

    protected final HistoricalCrudService<T, U> service;

    protected abstract void defineProperties();

    protected HistoricalCrudView(Class<T> clazz, HistoricalCrudService<T, U> service, HistoryCrudService<T, U> hisoryService) {
        this.service = service;

        setSizeFull();

        grid = new Grid<>();
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> {
            Hibernate.initialize(event.getValue());
            select(event.getValue());
        });
        addToPrimary(grid);

        crudForm = new CrudForm<>(clazz, service);
        crudForm.getElement().getStyle().set("padding", "10px");
        crudForm.setCreateListener(entity -> {
            fillGrid();
            clear();
            showNotification("Created!");
        });
        crudForm.setSaveListener(entity -> {
            fillGrid();
            grid.select(entity);
            showNotification("Saved!");
        });
        crudForm.setErrorListener(errors -> errors.forEach(e -> showNotification(e.getErrorMessage(), true)));

        historyView = new HistoryView<>(hisoryService);
        historyView.getElement().getStyle().set("padding", "10px");

        SplitLayout split = new SplitLayout(crudForm, historyView);
        split.setWidth("300px");
        split.setOrientation(Orientation.VERTICAL);
        split.addSplitterDragendListener(event -> historyView.reload());
        addToSecondary(split);

        addSplitterDragendListener(event -> historyView.reload());
    }

    protected void select(T entity) {
        fillForm(entity);
        fillHistory(entity);
    }

    protected void clear() {
        clearForm();
        clearHistory();
    }

    private void fillGrid() {
        grid.setItems(service.findAll());
    }

    private void fillForm(T entity) {
        crudForm.getBinder().setBean(entity);
    }

    private void fillHistory(T entity) {
        historyView.load(entity);
    }

    private void clearForm() {
        crudForm.getBinder().setBean(null);
        crudForm.getBinder().readBean(null);
    }

    private void clearHistory() {
        historyView.clear();
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
