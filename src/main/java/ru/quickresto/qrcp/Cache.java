package ru.quickresto.qrcp;

import android.content.ContentResolver;
import android.content.Context;

public final class Cache {

    private static ContentResolver sContentResolver;

    private Cache() {
        throw new RuntimeException();
    }

    public static synchronized void initialize(Context context) {
        sContentResolver = context.getContentResolver();
    }

    public static ContentResolver getContentResolver() {
        return sContentResolver;
    }
}
