package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Diffable;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class HistoricalCrudService<T extends DataEntity & Historical<U> & Diffable<T>, U extends History> extends CrudService<T> {

    @Autowired
    private HistoryCrudService<T, U> hiistoryService;

    public abstract U createHistory(T entity, String text, boolean created, boolean edited, boolean deleted);

    @Override
    public T save(T entity) {
        T before = load(entity.getId());
        T after = super.save(entity);
        hiistoryService.create(createHistory(after, before.diff(after), false, true, false));
        return after;
    }

    @Override
    public T create(T entity) {
        T created = super.create(entity);
        hiistoryService.create(createHistory(created, null, true, false, false));
        return created;
    }

    @Override
    public void delete(T entity) {
        super.delete(entity);
        hiistoryService.create(createHistory(entity, null, false, false, true));
    }
}