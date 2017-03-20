package ru.quickresto.qrcp.mapper;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;

public final class ObservableMapper {

    private ObservableMapper() {
        throw new RuntimeException();
    }

    public static <T> Observable<List<T>> queryAll(Class<T> cls) {
        return queryAll(cls, null, null, null);
    }

    public static <T> Observable<List<T>> queryAll(final Class<T> cls, final String[] fields, final String[] operators, final String[] values) {
        return Observable.fromCallable(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return Mapper.queryAll(cls, fields, operators, values);
            }
        });
    }
}
