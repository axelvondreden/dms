package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;

import java.util.List;

public interface HistoricalCrudService<T extends DataEntity & Historical<U>, U extends History> extends CrudService<U> {

    List<U> getHistory(T entity);

}
