package com.gabdrahmanov;

import com.gabdrahmanov.entity.TestEntityOne;
import com.gabdrahmanov.utils.EntityGenerator;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

@SpringBootTest(classes = EntityAutofill.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BaseTest {

    @Test
    void test() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestEntityOne entityOne = new TestEntityOne();
        EntityGenerator entityGenerator = new EntityGenerator();
        Map<String, Object> stringObjectMap = entityGenerator.fillEntity(entityOne);
        int i = 2; // debug flag
        //Assertions.assertEquals(8, entityOne.getCost());
        //Assertions.assertEquals("7", entityOne.getTestEntityTwo().getName());
        //  Assertions.assertEquals("7", entityOne.getEntityThreeCollection());
        for (Field field : entityOne.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(entityOne);
            if (Collection.class.isAssignableFrom(field.getType())) { // Проверяем, является ли значение коллекцией
                Collection<?> collection = (Collection<?>) value; // Приводим к Collection
                if (!collection.isEmpty()) {
                    Object firstElement = collection.iterator().next();
                    Assertions.assertNotNull(firstElement);
                    throw new RuntimeException("Поле " + field.getName() + " не заполнено");
                }
            } else if (field.getType().isAnnotationPresent(Entity.class) ){
                Assertions.assertNotNull(field.get(entityOne));
            } else {
                Assertions.assertEquals(value, stringObjectMap.get(field.getName()));
            }
        }
    }
}
