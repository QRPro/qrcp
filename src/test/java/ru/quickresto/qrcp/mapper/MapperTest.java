package ru.quickresto.qrcp.mapper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.junit.Test;

import java.util.List;

import ru.quickresto.qrcp.Cache;
import ru.quickresto.qrcp.annotations.ResolverEntity;
import ru.quickresto.qrcp.annotations.ResolverField;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class MapperTest {

    @ResolverEntity("content://ru.evotor.evotorpos.inventory/Commodity")
    public static class Entity {

        public Entity() {}

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
    public void testQueryAll() throws Exception {
        ContentResolver contentResolver = mock(ContentResolver.class);

        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToNext())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(cursor.getColumnIndex(eq("ID")))
                .thenReturn(0);
        when(cursor.getColumnIndex(eq("NAME")))
                .thenReturn(1);
        when(cursor.getInt(0))
                .thenReturn(123)
                .thenReturn(456);
        when(cursor.getString(1))
                .thenReturn("TEST")
                .thenReturn("1234");

        when(contentResolver.query(any(Uri.class), any(String[].class), any(String.class), any(String[].class), isNull(String.class)))
            .thenReturn(cursor);

        Context context = mock(Context.class);
        when(context.getContentResolver())
                .thenReturn(contentResolver);
        Cache.initialize(context);

        List<Entity> entities = Mapper.queryAll(Entity.class);
        assertNotNull(entities);
        assertEquals(entities.size(), 2);
        assertNotNull(entities.get(0).getId());
        assertEquals(entities.get(0).getId().intValue(), 123);
        assertEquals(entities.get(0).getName(), "TEST");

        assertNotNull(entities.get(1).getId());
        assertEquals(entities.get(1).getId().intValue(), 456);
        assertEquals(entities.get(1).getName(), "1234");
    }
}
