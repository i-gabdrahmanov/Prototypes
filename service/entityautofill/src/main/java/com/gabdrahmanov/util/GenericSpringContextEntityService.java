package com.gabdrahmanov.util;

import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public interface GenericSpringContextEntityService {

    /**
     * Метод получения экземпляра репозитория для сущности
     *
     * @param clazz Entity class
     * @param <T>   Type of Entity class
     * @param <R>   Type of JpaRepository class
     * @return JpaRepository
     */
    default <T, R> R getRepo(Class<T> clazz) {
        String[] repoNames = ApplicationContextProvider.getApplicationContext()
                .getBeanNamesForType(ResolvableType.forClassWithGenerics(JpaRepository.class, clazz, Long.class));
        if (repoNames.length > 0) {
            return (R) ApplicationContextProvider.getApplicationContext().getBean(repoNames[0]);
        }
        return (R) new SimpleJpaRepository<T, Long>(clazz, ApplicationContextProvider.getEntityManager());
    }
}
