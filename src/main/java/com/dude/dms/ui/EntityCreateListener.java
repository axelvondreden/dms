package com.dude.dms.ui;

import com.dude.dms.backend.data.DataEntity;

public interface EntityCreateListener<T extends DataEntity> {

    void onCreate(T entity);
}