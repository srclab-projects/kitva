package xyz.srclab.common.bytecode.proxy;

import xyz.srclab.common.reflect.MethodBody;
import xyz.srclab.common.reflect.MethodDefinition;

import java.util.Arrays;

public interface ProxyClass<T> {

    static Builder<Object> newBuilder() {
        return Builder.newBuilder();
    }

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return Builder.newBuilder(superClass);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    interface Builder<T> {

        static Builder<Object> newBuilder() {
            return ProxyOperator.getInstance().newBuilder();
        }

        static <T> Builder<T> newBuilder(Class<T> superClass) {
            return ProxyOperator.getInstance().newBuilder(superClass);
        }

        default Builder<T> addInterfaces(Class<?>... interfaces) {
            return addInterfaces(Arrays.asList(interfaces));
        }

        Builder<T> addInterfaces(Iterable<Class<?>> interfaces);

        <R> Builder<T> overrideMethod(String name, Class<?>[] parameterTypes, MethodBody<R> methodBody);

        <R> Builder<T> overrideMethod(MethodDefinition<R> methodDefinition);

        ProxyClass<T> build();
    }
}
