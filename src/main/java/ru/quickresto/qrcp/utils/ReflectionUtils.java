package ru.quickresto.qrcp.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.quickresto.qrcp.annotations.ResolverEntity;
import ru.quickresto.qrcp.annotations.ResolverField;

public class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static String getFieldDeclaredName(Class<?> type, String name) throws NoSuchFieldException {
        Field field = findFieldByName(type, name);

        if (field.isAnnotationPresent(ResolverField.class)) {
            return field.getAnnotation(ResolverField.class).name();
        }

        return null;
    }

    private static Field findFieldByName(Class<?> cls, String name) throws NoSuchFieldException {
        Class<?> c = cls;
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException e) {}
            c = c.getSuperclass();
        }
        throw new NoSuchFieldException(name);
    }

    public static void invokeSetter(Object object, Field field, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = findMethodByName(object.getClass(), "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());

        method.invoke(object, value);
    }

    private static Method findMethodByName(Class<?> cls, String name, Class<?> fieldType) throws NoSuchMethodException {
        Class<?> c = cls;
        while (c != null) {
            try {
                return c.getMethod(name, fieldType);
            } catch (NoSuchMethodException e) {}
            c = c.getSuperclass();
        }
        throw new NoSuchMethodException(name);
    }

    public static List<Field> getDeclaredColumnFields(Class<?> type) {
        List<Field> declaredColumnFields = new ArrayList<>();

        Field[] fields = type.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ResolverField.class)) {
                declaredColumnFields.add(field);
            }
        }

        Class<?> parentType = type.getSuperclass();
        if (parentType != null) {
            declaredColumnFields.addAll(getDeclaredColumnFields(parentType));
        }

        return declaredColumnFields;
    }
}
