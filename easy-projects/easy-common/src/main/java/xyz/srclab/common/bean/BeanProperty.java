package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

@Immutable
public interface BeanProperty extends BeanMember {

    Class<?> getType();

    Type getGenericType();

    boolean isReadable();

    @Nullable
    Object getValue(Object bean) throws UnsupportedOperationException;

    boolean isWriteable();

    void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException;
}
