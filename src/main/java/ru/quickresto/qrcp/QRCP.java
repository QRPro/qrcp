package ru.quickresto.qrcp;

import android.content.Context;

public final class QRCP {

    private static Context sContext;

    public static void initialize(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
