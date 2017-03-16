package ru.quickresto.qrcp.mapper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import ru.quickresto.qrcp.Cache;
import ru.quickresto.qrcp.annotations.ResolverEntity;
import ru.quickresto.qrcp.annotations.ResolverField;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class MapperTest {

    enum EntityType {
        VARIANT1,
        VARIANT2
    }

    @ResolverEntity("content://ru.evotor.evotorpos.inventory/Commodity")
    public static class Entity {

        public Entity() {
        }

        @ResolverField("ID")
        private Integer id;

        @ResolverField("NAME")
        private String name;

        @ResolverField(("IS_AUTH"))
        private Boolean authorized;

        @ResolverField("DISCOUNT_FOR_USER")
        private BigDecimal discount;

        @ResolverField("ENTITY_TYPE")
        private EntityType type;

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

        public Boolean getAuthorized() {
            return authorized;
        }

        public void setAuthorized(Boolean authorized) {
            this.authorized = authorized;
        }

        public BigDecimal getDiscount() {
            return discount;
        }

        public void setDiscount(BigDecimal discount) {
            this.discount = discount;
        }

        public EntityType getType() {
            return type;
        }

        public void setType(EntityType type) {
            this.type = type;
        }
    }

    @Test
    public void testQueryAll() throws Exception {
        ContentResolver contentResolver = mock(ContentResolver.class);

        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(cursor.getColumnIndex(eq("ID")))
                .thenReturn(0)
                .thenReturn(0);
        when(cursor.getColumnIndex(eq("NAME")))
                .thenReturn(1)
                .thenReturn(1);
        when(cursor.getColumnIndex(eq("DISCOUNT_FOR_USER")))
                .thenReturn(2)
                .thenReturn(2);
        when(cursor.getColumnIndex(eq("IS_AUTH")))
                .thenReturn(3)
                .thenReturn(3);
        when(cursor.getColumnIndex(eq("ENTITY_TYPE")))
                .thenReturn(4)
                .thenReturn(4);

        when(cursor.getInt(anyInt()))
                .thenReturn(123)
                .thenReturn(1)
                .thenReturn(456)
                .thenReturn(0);
        when(cursor.getString(anyInt()))
                .thenReturn("TEST")
                .thenReturn("VARIANT1")
                .thenReturn("1234")
                .thenReturn("VARIANT2");
        when(cursor.getLong(eq(2)))
                .thenReturn(111L)
                .thenReturn(222L);

        when(contentResolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), isNull(String.class)))
            .thenReturn(cursor);

        Context context = mock(Context.class);
        when(context.getContentResolver())
                .thenReturn(contentResolver);
        Cache.initialize(context);

        List<Entity> entities = Mapper.queryAll(Entity.class);
        assertNotNull(entities);
        assertEquals(2, entities.size(), 2);
        assertNotNull(entities.get(0).getId());
        assertEquals(123, entities.get(0).getId().intValue());
        assertEquals("TEST", entities.get(0).getName());
        assertEquals(true, entities.get(0).getAuthorized());
        assertEquals(new BigDecimal("111"), entities.get(0).getDiscount());
        assertEquals(EntityType.VARIANT1, entities.get(0).getType());

        assertNotNull(entities.get(1).getId());
        assertEquals(456, entities.get(1).getId().intValue());
        assertEquals("1234", entities.get(1).getName());
        assertEquals(false, entities.get(1).getAuthorized());
        assertEquals(new BigDecimal("222"), entities.get(1).getDiscount());
        assertEquals(EntityType.VARIANT2, entities.get(1).getType());
    }
}
