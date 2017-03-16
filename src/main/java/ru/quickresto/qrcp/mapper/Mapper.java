package ru.quickresto.qrcp.mapper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.quickresto.qrcp.Cache;
import ru.quickresto.qrcp.annotations.ResolverEntity;
import ru.quickresto.qrcp.utils.ReflectionUtils;

public final class Mapper {

    private Mapper() {
    }

    private static ContentResolver getContentResolver() {
        return Cache.getContentResolver();
    }

    private static Uri getUri(String url) {
        return Uri.parse(url);
    }

    public static void insert(Object object) throws IllegalAccessException, NoSuchFieldException {
        if (object.getClass().isAnnotationPresent(ResolverEntity.class)) {
            Uri uri = getUri(object.getClass().getAnnotation(ResolverEntity.class).value());

            ContentValues values = new ContentValues();

            List<Field> fields = ReflectionUtils.getDeclaredColumnFields(object.getClass());
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                String fieldDeclaredName = ReflectionUtils.getFieldDeclaredName(object.getClass(), field.getName());

                if (fieldType.isAssignableFrom(Integer.class)) {
                    values.put(fieldDeclaredName, (Integer) field.get(object));
                } else if (fieldType.isAssignableFrom(String.class)) {
                    values.put(fieldDeclaredName, (String) field.get(object));
                } /* else if (fieldType.isAssignableFrom(BigDecimal.class)) {
                        values.put(fieldDeclaredName, (BigDecimal) field.get(object));
                    }*/ else if (fieldType.isAssignableFrom(Boolean.class)) {
                    values.put(fieldDeclaredName, (Boolean) field.get(object));
                } else if (fieldType.isEnum()) {
                    values.put(fieldDeclaredName, field.get(object).toString());
                }
            }

            getContentResolver().insert(uri, values);
        }
    }

    public static <T> T query(Class<T> cls)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        return null;
    }

    public static <T> List<T> queryAll(Class<T> cls)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, NoSuchFieldException {
        return queryAll(cls, null, null);
    }

    public static <T> List<T> queryAll(Class<T> cls, String selection, String[] selectionArgs)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, NoSuchFieldException {
        List<T> result = new ArrayList<>();

        if (cls.isAnnotationPresent(ResolverEntity.class)) {
            Uri uri = getUri(cls.getAnnotation(ResolverEntity.class).value());

            Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {

                        T entity = cls.newInstance();
                        for (Field field : ReflectionUtils.getDeclaredColumnFields(cls)) {
                            parseField(cursor, entity, cls, field);
                        }
                        result.add(entity);
                    }
                } finally {
                    cursor.close();
                }
            }

        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> void parseField(Cursor cursor, T entity, Class<T> cls, Field field)
            throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> fieldType = field.getType();
        String fieldDeclaredName = ReflectionUtils.getFieldDeclaredName(cls, field.getName());
        Object value = null;
        if (fieldType.isAssignableFrom(Integer.class)) {
            value = cursor.getColumnIndex(fieldDeclaredName) > -1
                    ? cursor.getInt(cursor.getColumnIndex(fieldDeclaredName))
                    : null;
        } else if (fieldType.isAssignableFrom(String.class)) {
            value = cursor.getColumnIndex(fieldDeclaredName) > -1
                    ? cursor.getString(cursor.getColumnIndex(fieldDeclaredName))
                    : null;
        } else if (fieldType.isAssignableFrom(BigDecimal.class)) {
            value = cursor.getColumnIndex(fieldDeclaredName) > -1
                    ? new BigDecimal(cursor.getLong(cursor.getColumnIndex(fieldDeclaredName)))
                    : null;
        } else if (fieldType.isAssignableFrom(Boolean.class)) {
            value = cursor.getColumnIndex(fieldDeclaredName) > -1
                    ? cursor.getInt(cursor.getColumnIndex(fieldDeclaredName)) == 1
                    : null;
        } else if (fieldType.isEnum()) {
            value = cursor.getColumnIndex(fieldDeclaredName) > -1
                    ? Enum.valueOf((Class<Enum>) fieldType, cursor.getString(cursor.getColumnIndex(fieldDeclaredName)))
                    : null;
        }

        ReflectionUtils.invokeSetter(entity, field, value);
    }
}
