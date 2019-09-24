package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

@FunctionalInterface
public interface CrudService<T extends DataEntity> {

    JpaRepository<T, Long> getRepository();

    default T save(User currentUser, T entity) {
        return getRepository().saveAndFlush(entity);
    }

    default void delete(User currentUser, T entity) {
        if (entity == null) {
            throw new EntityNotFoundException("entity was null");
        }
        getRepository().delete(entity);
    }

    default void delete(User currentUser, long id) {
        delete(currentUser, load(id));
    }

    default long count() {
        return getRepository().count();
    }

    default T load(long id) {
        T entity = getRepository().findById(id).orElse(null);
        if (entity == null) {
            throw new EntityNotFoundException("entity was null");
        }
        return entity;
    }
}
