package ru.quickresto.qrcp.utils;

import org.junit.Test;

import ru.quickresto.qrcp.annotations.ResolverField;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FilterUtilsTest {

    class Entity {

        @ResolverField("testName")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testBuildFilter() {
        String selection = null;
        try {
            selection = FilterUtils.buildFilter(Entity.class, new String[]{"name"}, new String[]{"="});
        } catch (Throwable e) {}

        assertNotNull(selection);
        assertTrue(selection.contains("testName"));
    }
}
