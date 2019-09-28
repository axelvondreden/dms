package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import com.dude.dms.backend.service.HistoricalCrudService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class CrudHistoryView<T extends DataEntity & Historical<U>, U extends History> extends VerticalLayout {

    private final HistoricalCrudService<T, U> service;

    public CrudHistoryView(HistoricalCrudService<T, U> service) {
        this.service = service;
    }

    protected void load(T entity) {
        List<U> list = service.getHistory(entity);
        for (U history : list) {
            add(history.getText());
        }
    }

    protected void clear() {
        removeAll();
    }
}
