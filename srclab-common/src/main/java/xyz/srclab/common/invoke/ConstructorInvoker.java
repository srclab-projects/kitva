package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.reflect.ConstructorKit;

import java.lang.reflect.Constructor;

@Immutable
public interface ConstructorInvoker<T> {

    static <T> ConstructorInvoker<T> of(Constructor<T> constructor) {
        return InvokerSupport.getConstructorInvoker(constructor);
    }

    static <T> ConstructorInvoker<T> of(Class<T> type, Class<?>... parameterTypes) {
        @Nullable Constructor<T> constructor = ConstructorKit.getConstructor(type, parameterTypes);
        Check.checkState(constructor != null, () ->
                "Constructor not found: " + Describer.describeConstructor(type, parameterTypes));
        return of(constructor);
    }

    Constructor<T> getConstructor();

    T invoke(Object... args);
}