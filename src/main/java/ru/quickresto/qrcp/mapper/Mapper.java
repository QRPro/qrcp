package ru.quickresto.qrcp.mapper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.quickresto.qrcp.Cache;
import ru.quickresto.qrcp.annotations.ResolverEntity;
import ru.quickresto.qrcp.exceptions.InsertException;
import ru.quickresto.qrcp.exceptions.QueryException;
import ru.quickresto.qrcp.utils.FilterUtils;
import ru.quickresto.qrcp.utils.ReflectionUtils;

public final class Mapper {

    private Mapper() {
        throw new RuntimeException();
    }

    private static ContentResolver getContentResolver() {
        return Cache.getContentResolver();
    }

    private static Uri getUri(String url) {
        return Uri.parse(url);
    }

    public static void insert(Object object) {
        try {
            if (object.getClass().isAnnotationPresent(ResolverEntity.class)) {
                Uri uri = getUri(object.getClass().getAnnotation(ResolverEntity.class).value());

                ContentValues values = new ContentValues();

                List<Field> fields = ReflectionUtils.getDeclaredColumnFields(object.getClass());
                for (Field field : fields) {
                    Class<?> fieldType = field.getType();
                    String fieldDeclaredName = ReflectionUtils.getFieldDeclaredName(object.getClass(), field.getName());

                    if (fieldType.isAssignableFrom(Integer.class)) {
                        values.put(fieldDeclaredName, (Integer) ReflectionUtils.invokeGetter(object, field));
                    } else if (fieldType.isAssignableFrom(String.class)) {
                        values.put(fieldDeclaredName, (String) ReflectionUtils.invokeGetter(object, field));
                    } else if (fieldType.isAssignableFrom(BigDecimal.class)) {
                        BigDecimal value = (BigDecimal) ReflectionUtils.invokeGetter(object, field);
                        values.put(fieldDeclaredName, value != null ? value.toString() : null);
                    } else if (fieldType.isAssignableFrom(Boolean.class)) {
                        values.put(fieldDeclaredName, (Boolean) ReflectionUtils.invokeGetter(object, field));
                    } else if (fieldType.isEnum()) {
                        Enum value = (Enum) ReflectionUtils.invokeGetter(object, field);
                        values.put(fieldDeclaredName, value != null ? value.toString() : null);
                    }
                }

                getContentResolver().insert(uri, values);
            }
        } catch (Throwable e) {
            Log.e(Mapper.class.getName(), e.getLocalizedMessage(), e);
            throw new InsertException(e.getLocalizedMessage());
        }
    }

    public static <T> List<T> select(Class<T> cls) {
        return select(cls, null, null, null);
    }

    public static <T> List<T> select(Class<T> cls, String[] fields, String[] operators, String[] values) {
        List<T> result = new ArrayList<>();

        try {
            if (cls.isAnnotationPresent(ResolverEntity.class)) {
                Uri uri = getUri(cls.getAnnotation(ResolverEntity.class).value());

                String selection = null;
                if (fields != null) {
                    if (operators == null || values == null || operators.length != fields.length || values.length != fields.length) {
                        throw new IllegalArgumentException("Invalid filter arrays");
                    }

                    selection = FilterUtils.buildFilter(cls, fields, operators);
                }

                Cursor cursor = getContentResolver().query(uri, null, selection, values, null);
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
        } catch (Throwable e) {
            Log.e(Mapper.class.getName(), e.getLocalizedMessage(), e);
            throw new QueryException(e.getLocalizedMessage());
        }

        return result;
    }
    
    public static <T> void delete(Class<T> cls) {
        delete(cls, null, null, null);
    }
    
    public static <T> void delete(Class<T> cls, String[] fields, String[] operators, String[] values) {
        try {
            if (cls.isAnnotationPresent(ResolverEntity.class)) {
                Uri uri = getUri(cls.getAnnotation(ResolverEntity.class).value());

                String where = null;
                if (fields != null) {
                    if (operators == null || values == null || operators.length != fields.length || values.length != fields.length) {
                        throw new IllegalArgumentException("Invalid filter arrays");
                    }

                    where = FilterUtils.buildFilter(cls, fields, operators);
                }

                getContentResolver().delete(uri, where, values);
            }
        } catch (Throwable e) {
            Log.e(Mapper.class.getName(), e.getLocalizedMessage(), e);
            throw new InsertException(e.getLocalizedMessage());
        }
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
