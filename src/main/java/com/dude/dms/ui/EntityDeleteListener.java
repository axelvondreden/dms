package com.dude.dms.ui;

import com.dude.dms.backend.data.DataEntity;

public interface EntityDeleteListener<T extends DataEntity> {

    void onDelete(T entity);
}