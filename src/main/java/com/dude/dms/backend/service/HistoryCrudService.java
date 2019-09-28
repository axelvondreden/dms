package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;

import java.util.List;

public abstract class HistoryCrudService<T extends DataEntity & Historical<U>, U extends History> extends CrudService<U> {

    public abstract List<U> getHistory(T entity);

}
