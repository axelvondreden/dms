package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import com.dude.dms.backend.service.HistoryCrudService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CrudHistoryView<T extends DataEntity & Historical<U>, U extends History> extends VerticalLayout {

    private final HistoryCrudService<T, U> service;

    public CrudHistoryView(HistoryCrudService<T, U> service) {
        this.service = service;
    }

    protected void load(T entity) {
        clear();
        for (U history : service.getHistory(entity)) {
            add(history.getText());
        }
    }

    protected void clear() {
        removeAll();
    }
}
