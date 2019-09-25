package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.DataEntity;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Locale;
import java.util.Optional;

public interface FilterableCrudService<T extends DataEntity> extends CrudService<T> {

    Page<T> findAnyMatching(Optional<String> filter, Pageable pageable);

    long countAnyMatching(Optional<String> filter);

    @SuppressWarnings("unchecked")
    default String getPK() {
        Class<T> genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), FilterableCrudService.class);
        return genericType != null ? genericType.getName().toLowerCase(Locale.ENGLISH) + "_id" : "id";
    }

}
