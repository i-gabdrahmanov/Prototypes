package com.gabdrahmanov.enums;

import com.gabdrahmanov.entity.RegistryEntity;
import com.gabdrahmanov.util.GenericSpringContextEntityService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Objects;

public interface DictionaryEnum<T extends RegistryEntity> extends GenericSpringContextEntityService {

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

    default T getEntityReferenceByEnum() {
        Class<? extends RegistryEntity> clazz = getEntityType();
        JpaRepository<T, Long> repository = getRepo(clazz);
        return repository.getReferenceById(getCode());
    }
}
