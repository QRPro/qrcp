package ru.quickresto.qrcp;

import android.content.Context;

public final class QRCP {

    private QRCP() {
    }

    public static void initialize(Context context) {
        Cache.initialize(context);
    }
}
