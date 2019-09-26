package com.dude.dms.backend.data.entity;

import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Field;
import java.util.Objects;

public interface Diffable<T extends DataEntity> {

    default String diff(T other) {
        StringBuilder diff = new StringBuilder();
        Field[] fields = GenericTypeResolver.resolveTypeArgument(getClass(), Diffable.class).getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!Objects.equals(field.get(this), field.get(other))) {
                    diff.append(field.getName()).append(": ").append(field.get(this)).append(" -> ").append(field.get(other)).append('\n');
                }
            } catch (IllegalAccessException e) {
                // ignore private fields
            }
        }
        return "Diff:\n" + diff;
    }
}
