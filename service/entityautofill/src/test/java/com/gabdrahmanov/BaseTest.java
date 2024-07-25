package com.gabdrahmanov;

import com.gabdrahmanov.entity.TestEntityOne;
import com.gabdrahmanov.utils.EntityGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.InvocationTargetException;

@SpringBootTest(classes = EntityAutofill.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BaseTest {

    @Test
    void test() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        TestEntityOne entityOne = new TestEntityOne();
        EntityGenerator entityGenerator = new EntityGenerator();
        entityGenerator.fillEntity(entityOne);
        int i =2;
        //Assertions.assertEquals(8, entityOne.getCost());
        //Assertions.assertEquals("7", entityOne.getTestEntityTwo().getName());
        Assertions.assertEquals("7", entityOne.getEntityThreeCollection());
    }
}
