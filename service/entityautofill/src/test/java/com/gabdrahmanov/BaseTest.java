package com.gabdrahmanov;

import com.gabdrahmanov.entity.TestEntityOne;
import com.gabdrahmanov.utils.EntityGenerator;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SpringBootTest(classes = EntityAutofill.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BaseTest {

    @Autowired
    private EntityGenerator entityGenerator;

    @Autowired
    private Set<Class<?>> entityClasses;

    @Test
    void test() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {


        for (Class<?> clazz : entityClasses) {
            Object o = clazz.getDeclaredConstructor().newInstance();
            Map<String, Object> stringObjectMap = entityGenerator.fillEntity(o);
            int i = 2; // debug flag
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
}
