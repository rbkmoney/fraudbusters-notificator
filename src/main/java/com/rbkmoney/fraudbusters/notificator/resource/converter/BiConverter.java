package com.rbkmoney.fraudbusters.notificator.resource.converter;

public interface BiConverter<S, T> {

    T toTarget(S s);

    S toSource(T t);
}
