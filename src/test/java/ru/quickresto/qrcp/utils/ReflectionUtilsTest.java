package ru.quickresto.qrcp.utils;


import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import ru.quickresto.qrcp.annotations.ResolverField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReflectionUtilsTest {

    class Entity {

        @ResolverField("ID")
        private Integer id;

        @ResolverField("NAME")
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void test() {
        List<Field> fields = ReflectionUtils.getDeclaredColumnFields(Entity.class);
        assertNotNull(fields);
        assertEquals(2, fields.size());
        assertEquals("id", fields.get(0).getName());
        assertEquals("name", fields.get(1).getName());
    }

}
