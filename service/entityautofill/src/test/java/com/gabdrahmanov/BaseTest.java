package com.gabdrahmanov;

import com.gabdrahmanov.utils.EntityGenerator;
import com.google.common.collect.Iterables;
import jakarta.persistence.Entity;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = EntityAutofill.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BaseTest {

    @Autowired
    private EntityGenerator entityGenerator;

    @Autowired
    private Set<Class<?>> entityClasses;

    @Test
    @DisplayName("Базовый тест автозаполнрения сущностей")
    void testEntityFill() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (Class<?> clazz : entityClasses) {
            Object o = clazz.getDeclaredConstructor().newInstance();
            Map<String, Object> stringObjectMap = entityGenerator.fillEntity(o);
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(o);
                if (Collection.class.isAssignableFrom(field.getType())) {
                    Collection<?> collection = (Collection<?>) value;
                    if (!collection.isEmpty()) {
                        Object firstElement = collection.iterator().next();
                        Assertions.assertNotNull(firstElement);
                    } else {
                        throw new RuntimeException("Поле " + field.getName() + " не заполнено");
                    }
                } else if (field.getType().isAnnotationPresent(Entity.class)) {
                    Assertions.assertNotNull(field.get(o));
                } else {
                    Assertions.assertEquals(value, stringObjectMap.get(field.getName()));
                }
            }
        }
    }

    @Test
    @DisplayName("Тест автозаполнения сущностей через методы")
    void entityFillTestByReflectionMethods() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<List<Class<?>>> lists = Lists.newArrayList(Iterables.partition(entityClasses, 10));

        // В один поток не успевает забирать значения, приходится использовать несколько потоков
        lists.forEach(list -> executorService.execute(() -> {
            for (Class<?> entityClass : entityClasses) {
                Object o;
                try {
                    o = entityClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                Map<String, Object> stringObjectMap;
                try {
                    stringObjectMap = entityGenerator.fillEntity(o);
                } catch (InvocationTargetException | InstantiationException | NoSuchMethodException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                Object testedEntity;
                try {
                    testedEntity = entityClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                List<Method> methods = Arrays.asList(testedEntity.getClass().getDeclaredMethods());
                for (Field field : testedEntity.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Class<?> fieldType = field.getType();
                    Object extractedValue = stringObjectMap.get(fieldName);
                    String getterMethodName;
                    String setterMethodName;
                    if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
                        StringBuilder booleanStringBuilder = new StringBuilder(fieldName.substring(1));
                        booleanStringBuilder.setCharAt(0, Character.toUpperCase(booleanStringBuilder.charAt(0)));
                        StringBuilder sbSet = new StringBuilder(fieldName.substring(2));
                        sbSet.setCharAt(0, Character.toUpperCase(sbSet.charAt(0)));
                        getterMethodName = "is" + booleanStringBuilder;
                        setterMethodName = "set" + sbSet;
                    } else {
                        StringBuilder sb = new StringBuilder(fieldName);
                        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                        getterMethodName = "get" + sb;
                        setterMethodName = "set" + sb;
                    }
                    Method targetSetMethod = methods.stream()
                            .filter(method -> method.getName().toLowerCase().contains("set") && method.getName().toLowerCase()
                                    .contains(fieldName.toLowerCase()))
                            .findAny()
                            .orElseThrow(() -> new RuntimeException(String.format("No value Present: %s on setter: %s", testedEntity.getClass().getSimpleName(), setterMethodName)));
                    Method targetGetMethod = methods.stream()
                            .filter(method -> method.getName().toLowerCase().contains("get") && method.getName().toLowerCase()
                                    .contains(fieldName.toLowerCase()))
                            .findAny()
                            .orElseThrow(() -> new RuntimeException(String.format("No value Present: %s on getter: %s", testedEntity.getClass().getSimpleName(), getterMethodName)));
                    try {
                        targetGetMethod.setAccessible(true);
                        if (fieldType.isAnnotationPresent(Entity.class)) {
                            targetSetMethod.invoke(testedEntity, field.getType().getDeclaredConstructor().newInstance());
                            Assertions.assertNotNull(targetGetMethod.invoke(testedEntity));
                        } else if (Collection.class.isAssignableFrom(fieldType)) {
                            targetSetMethod.invoke(testedEntity, new ArrayList<>());
                            Assertions.assertNotNull(targetGetMethod.invoke(testedEntity));
                        } else {
                            targetSetMethod.invoke(testedEntity, extractedValue);
                            Thread.sleep(500);
                            Assertions.assertNotNull(targetGetMethod.invoke(testedEntity));
                            Assertions.assertEquals(stringObjectMap.get(fieldName), targetGetMethod.invoke(testedEntity));
                        }
                    } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException |
                             InterruptedException | InstantiationException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }));
    }
}

