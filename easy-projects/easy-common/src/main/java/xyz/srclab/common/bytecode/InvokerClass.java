package xyz.srclab.common.bytecode;

import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

/**
 * @author sunqian
 */
public interface InvokerClass<T> {

    static <T> InvokerClass<T> ofType(Class<T> type) {
        return ByteCodeSupport.getInvokerClass(type);
    }

    ConstructorInvoker<T> getConstructorInvoker(Class<?>... parameterTypes);

    MethodInvoker getMethodInvoker(String methodName, Class<?>... parameterTypes);
}