package com.gabdrahmanov.utils;

import jakarta.persistence.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EntityGenerator {

    public void fillEntity(Object entity) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        fillRandomly(entity, entity.getClass());
    }

    private void fillRandomly(Object entity, Class<?> parentEntity) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Class<?> entityClass = entity.getClass();
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            String a = field.getType().getName();
            String b = parentEntity.getName();
            int cfghj = 3;
            try {
                if (!field.getType().getName().equals(parentEntity.getName())) {
                    Class<?> fieldType = field.getType();

                    // Проверка на вложенную Entity
                    if (fieldType.isAnnotationPresent(Entity.class)) {
                        Object nestedEntity = fieldType.getDeclaredConstructor().newInstance();
                        fillRandomly(nestedEntity, parentEntity); // Рекурсивный вызов для заполнения вложенной сущности
                        field.set(entity, nestedEntity);
                    }
                    // Проверка на Collection
                    else if (Collection.class.isAssignableFrom(fieldType)) {
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        Class<?> elementType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                        // Создание экземпляра конкретной реализации коллекции, например, ArrayList
                        Collection collection;
                        if (Set.class.isAssignableFrom(fieldType)) {
                            collection = new HashSet(); // или другой подходящий класс
                        } else if (LinkedList.class.isAssignableFrom(fieldType)) {

                            collection = new LinkedList();
                        } else {
                            collection = new ArrayList();
                        }
                        if (elementType.isAnnotationPresent(Entity.class)) {
                            // Создание случайного количества вложенных сущностей
                            int randomCount = ThreadLocalRandom.current().nextInt(1, 5);
                            for (int i = 0; i < randomCount; i++) {
                                Object nestedEntity = elementType.getDeclaredConstructor().newInstance();
                                fillRandomly(nestedEntity, parentEntity); // Рекурсивный вызов для заполнения вложенной сущности
                                collection.add(nestedEntity);
                            }
                            field.set(entity, collection);
                        }
                        // Проверка на примитивные типы и String
                        else if (fieldType.isPrimitive() || fieldType == String.class) {
                            if (fieldType == boolean.class) {
                                field.setBoolean(entity, ThreadLocalRandom.current().nextBoolean());
                            } else if (fieldType == int.class) {
                                field.setInt(entity, ThreadLocalRandom.current().nextInt());
                            } else if (fieldType == long.class) {
                                field.setLong(entity, ThreadLocalRandom.current().nextLong());
                            } else if (fieldType == double.class) {
                                field.setDouble(entity, ThreadLocalRandom.current().nextDouble());
                            } else if (fieldType == String.class) {
                                field.set(entity, UUID.randomUUID().toString());
                            }
                        }

                        else if (fieldType.isEnum()) {
                            Object[] enumConstants = fieldType.getEnumConstants();
                            field.set(entity, enumConstants[ThreadLocalRandom.current().nextInt(enumConstants.length)]);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка при заполнении поля " + field.getName() + ": " + e.getMessage());
                throw e;
            }
        }
    }
}