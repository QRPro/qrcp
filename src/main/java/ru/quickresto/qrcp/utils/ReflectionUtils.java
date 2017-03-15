package ru.quickresto.qrcp.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static List<Field> getDeclaredColumnFields(Class<?> type, Class<? extends Annotation> annotationType) {
        List<Field> declaredColumnFields = Collections.emptyList();

        Field[] fields = type.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationType)) {
                declaredColumnFields.add(field);
            }
        }

        Class<?> parentType = type.getSuperclass();
        if (parentType != null) {
            declaredColumnFields.addAll(getDeclaredColumnFields(parentType, annotationType));
        }

        return declaredColumnFields;
    }
}
