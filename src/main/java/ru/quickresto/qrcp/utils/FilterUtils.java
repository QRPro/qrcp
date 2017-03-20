package ru.quickresto.qrcp.utils;

public class FilterUtils {

    private FilterUtils() {
        throw new RuntimeException();
    }

    public static String buildFilter(String[] fields, String[] operators) {
        StringBuilder selection = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            selection.append(fields[i])
                    .append(operators[i])
                    .append("?");
        }

        return selection.toString();
    }
}
