package ru.quickresto.qrcp.mapper;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import ru.quickresto.qrcp.Cache;
import ru.quickresto.qrcp.annotations.ResolverEntity;

public final class Mapper {

    private Mapper() {
    }

    private static ContentResolver getContentResolver() {
        return Cache.getContentResolver();
    }

    private static Uri getUri(String url) {
        return Uri.parse(url);
    }

    public static <T> T query(Class<T> cls) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        return null;
    }

    public static <T> List<T> queryAll(Class<T> cls) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        return queryAll(cls, null, null);
    }

    public static <T> List<T> queryAll(Class<T> cls, String selection, String[] selectionArgs) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        List<T> result = new ArrayList<>();

        if (cls.isAnnotationPresent(ResolverEntity.class)) {
            Uri uri = getUri(cls.getAnnotation(ResolverEntity.class).url());
            String[] fields = {};

            Cursor cursor = getContentResolver().query(uri, fields, selection, selectionArgs, null);
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        Constructor<T> constructor = cls.getConstructor(Cursor.class);
                        result.add(constructor.newInstance(cursor));
                    }
                } finally {
                    cursor.close();
                }
            }

        }

        return result;
    }
}
