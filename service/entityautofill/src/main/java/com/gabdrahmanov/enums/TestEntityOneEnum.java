package com.gabdrahmanov.enums;

import com.gabdrahmanov.entity.RegistryEntity;
import com.gabdrahmanov.entity.TestEntityOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestEntityOneEnum implements DictionaryEnum<TestEntityOne> {
    ONE(1L, "First", 1000),
    TWO(2L, "Second", 2000),
    THREE(3L, "Third", 3000);

    private final Long code;
    private final String name;
    private final int cost;


    @Override
    public Class<? extends RegistryEntity> getEntityType() {
        return TestEntityOne.class;
    }
}
