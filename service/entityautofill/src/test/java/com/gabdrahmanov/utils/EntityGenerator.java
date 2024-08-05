package com.gabdrahmanov.utils;

import jakarta.persistence.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EntityGenerator {

    public Map<String, Object> fillEntity(Object entity) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return fillRandomly(entity, entity.getClass());
    }

    private Map<String, Object> fillRandomly(Object entity, Class<?> parentEntity) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Map<String, Object> generatedFieldValues = new HashMap<>();
        Class<?> entityClass = entity.getClass();
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getType().getName().equals(parentEntity.getName())) {
                    Object nestedEntity = field.getType().getDeclaredConstructor().newInstance();
                    field.set(entity, nestedEntity);
                } else {
                    Class<?> fieldType = field.getType();

                    // Проверка на вложенную Entity
                    if (fieldType.isAnnotationPresent(Entity.class)) {
                        Object nestedEntity = fieldType.getDeclaredConstructor().newInstance();
                        fillRandomly(nestedEntity, parentEntity); // Рекурсивный вызов для заполнения вложенной сущности
                        field.set(entity, nestedEntity);
                        generatedFieldValues.put(field.getName(), entity);
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
                                fillRandomly(nestedEntity, parentEntity);
                                generatedFieldValues.put(field.getName(), entity);// Рекурсивный вызов для заполнения вложенной сущности
                                collection.add(nestedEntity);
                            }
                            field.set(entity, collection);
                        }
                    }
                    // Проверка на примитивные типы и String
                    else if (fieldType.isPrimitive() || fieldType == String.class) {
                        Object value = new Object();
                        if (fieldType == boolean.class) {
                            value = ThreadLocalRandom.current().nextBoolean();
                            field.setBoolean(entity, (boolean) value);
                        } else if (fieldType == int.class) {
                            value = ThreadLocalRandom.current().nextInt();
                            field.setInt(entity, (int) value);
                        } else if (fieldType == long.class) {
                            value = ThreadLocalRandom.current().nextLong();
                            field.setLong(entity, (long) value);
                        } else if (fieldType == double.class) {
                            value = ThreadLocalRandom.current().nextDouble();
                            field.setDouble(entity, (double) value);
                        } else if (fieldType == String.class) {
                            value = UUID.randomUUID().toString();
                            field.set(entity, value);
                        }
                        generatedFieldValues.put(field.getName(), value);
                    } else if (fieldType.isEnum()) {
                        Object[] enumConstants = fieldType.getEnumConstants();
                        Object enumConstant = enumConstants[ThreadLocalRandom.current().nextInt(enumConstants.length)];
                        field.set(entity, enumConstant);
                        generatedFieldValues.put(field.getName(), enumConstant);
                    }
                }

            } catch (Exception e) {
                System.err.println("Ошибка при заполнении поля " + field.getName() + ": " + e.getMessage());
                throw e;
            }
        }
        return generatedFieldValues;
    }
}