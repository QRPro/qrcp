package ru.quickresto.qrcp.mapper;

public abstract class BaseResolverEntity {

    public void insert() throws IllegalAccessException, NoSuchFieldException {
        Mapper.insert(this);
    }
}
