package com.gabdrahmanov.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;

import java.util.Objects;

public interface DictionaryEnum<T extends RegistryEntity> {

    Long getCode();

    Class<? extends RegistryEntity> getEntityType();

    default boolean equalsTo(T entity) {
        if (!Hibernate.isInitialized(entity)) {
            Hibernate.initialize(entity);
        }
        return entity != null && Objects.equals(entity.getId(), getCode());
    }

    default boolean equalsTo(String code) {
        return StringUtils.equals(code, getCode().toString());
    }
}
