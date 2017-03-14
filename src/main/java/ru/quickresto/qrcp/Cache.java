package ru.quickresto.qrcp;

import android.content.Context;

public final class Cache {

    private static Context sContext;

    private Cache() {
    }

    public static synchronized void initialize(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
