package ru.quickresto.qrcp.mapper;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;

public final class ObservableMapper {

    private ObservableMapper() {
    }

    public static <T> Observable<List<T>> queryAll(Class<T> cls) {
        return queryAll(cls, null, null);
    }

    public static <T> Observable<List<T>> queryAll(final Class<T> cls, final String selection, final String[] selectionArgs) {
        return Observable.fromCallable(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return Mapper.queryAll(cls, selection, selectionArgs);
            }
        });
    }
}
