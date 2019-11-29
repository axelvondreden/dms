package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.EntityDeleteListener;
import com.dude.dms.ui.EntityEditListener;
import com.vaadin.flow.component.dialog.Dialog;

public abstract class EventDialog<T extends DataEntity> extends Dialog {

    private EntityCreateListener<T> createListener;

    private EntityEditListener<T> editListener;

    private EntityDeleteListener<T> deleteListener;

    public void setCreateListener(EntityCreateListener<T> createListener) {
        this.createListener = createListener;
    }

    public void setDeleteListener(EntityDeleteListener<T> deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setEditListener(EntityEditListener<T> editListener) {
        this.editListener = editListener;
    }

    protected void triggerCreateEvent(T entity) {
        if (createListener != null) {
            createListener.onCreate(entity);
        }
    }

    protected void triggerDeleteEvent(T entity) {
        if (deleteListener != null) {
            deleteListener.onDelete(entity);
        }
    }

    protected void triggerEditEvent(T entity) {
        if (editListener != null) {
            editListener.onEdit(entity);
        }
    }
}