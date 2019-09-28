package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public abstract class CrudService<T extends DataEntity> {

    protected abstract JpaRepository<T, Long> getRepository();

    public T save(T entity) {
        return getRepository().saveAndFlush(entity);
    }

    public void delete(T entity) {
        if (entity == null) {
            throw new EntityNotFoundException("entity was null");
        }
        getRepository().delete(entity);
    }

    public long count() {
        return getRepository().count();
    }

    public T load(long id) {
        T entity = getRepository().findById(id).orElse(null);
        if (entity == null) {
            throw new EntityNotFoundException("entity was null");
        }
        return entity;
    }

    public T create(T entity) {
        return getRepository().saveAndFlush(entity);
    }

    public List<T> findAll() {
        return getRepository().findAll();
    }
}
