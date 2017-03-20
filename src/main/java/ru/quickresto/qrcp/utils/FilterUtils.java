package ru.quickresto.qrcp.utils;

public class FilterUtils {

    private FilterUtils() {
        throw new RuntimeException();
    }

    public static String buildFilter(Class<?> type, String[] fields, String[] operators) throws NoSuchFieldException {
        StringBuilder selection = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            selection.append(ReflectionUtils.getFieldDeclaredName(type, fields[i]))
                    .append(operators[i])
                    .append("?");
        }

        return selection.toString();
    }
}
