package com.dude.dms.ui;

import com.dude.dms.backend.data.DataEntity;

public interface EntityEditListener<T extends DataEntity> {

    void onEdit(T entity);
}