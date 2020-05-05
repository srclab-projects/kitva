package xyz.srclab.common.reflect.method;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.base.Shares;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.collection.list.ListHelper;
import xyz.srclab.common.lang.key.Key;
import xyz.srclab.common.reflect.NullRole;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodHelper {

    private static final Joiner PARAMETER_TYPES_JOINER = Shares.COMMA_JOINER;

    private static final Cache<Key, Method> methodCache = Cache.newGcThreadLocalL2();

    private static final Cache<Key, List<Method>> methodsCache = Cache.newGcThreadLocalL2();

    @Nullable
    public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Method result = methodCache.getNonNull(
                Key.from(cls, methodName, parameterTypes),
                k -> getMethod0(cls, methodName, parameterTypes)
        );
        if (result == NullRole.getNullMethod()) {
            return null;
        }
        return result;
    }

    private static Method getMethod0(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        try {
            return cls.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return NullRole.getNullMethod();
        }
    }

    @Immutable
    public static List<Method> getAllMethods(Class<?> cls) {
        return methodsCache.getNonNull(
                Key.from("getAllMethods", cls),
                k -> ListHelper.immutable(getAllMethods0(cls))
        );
    }

    private static List<Method> getAllMethods0(Class<?> cls) {
        List<Method> result = new LinkedList<>();
        Class<?> current = cls;
        do {
            result.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        } while (current != null);
        return result;
    }

    @Immutable
    public static List<Method> getOverrideableMethods(Class<?> cls) {
        return methodsCache.getNonNull(
                Key.from("getOverrideableMethods", cls),
                k -> ListHelper.immutable(getOverrideableMethods0(cls))
        );
    }

    private static List<Method> getOverrideableMethods0(Class<?> cls) {
        List<Method> result = ListHelper.concat(
                Arrays.asList(cls.getMethods()),
                Arrays.asList(cls.getDeclaredMethods())
        );
        return result.stream()
                .distinct()
                .filter(MethodHelper::canOverride)
                .collect(Collectors.toList());
    }

    @Immutable
    public static List<Method> getPublicStaticMethods(Class<?> cls) {
        return methodsCache.getNonNull(
                Key.from("getPublicStaticMethods", cls),
                k -> ListHelper.immutable(getPublicStaticMethods0(cls))
        );
    }

    private static List<Method> getPublicStaticMethods0(Class<?> cls) {
        return Arrays.stream(cls.getMethods())
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    @Immutable
    public static List<Method> getPublicNonStaticMethods(Class<?> cls) {
        return methodsCache.getNonNull(
                Key.from("getPublicNonStaticMethods", cls),
                kc -> ListHelper.immutable(getPublicNonStaticMethods0(cls))
        );
    }

    private static List<Method> getPublicNonStaticMethods0(Class<?> cls) {
        return Arrays.stream(cls.getMethods())
                .filter(m -> !m.isBridge() && !Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    public static boolean canOverride(Method method) {
        int modifiers = method.getModifiers();
        return !method.isBridge()
                && !Modifier.isStatic(modifiers)
                && !Modifier.isFinal(modifiers)
                && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers));
    }

    public static String methodToString(Class<?> declaringClass, String methodName, Class<?>... parameterTypes) {
        return declaringClass.getName() +
                "." +
                methodName +
                "(" +
                PARAMETER_TYPES_JOINER.join(
                        ArrayHelper.buildArray(new String[parameterTypes.length], i -> parameterTypes[i].getName())
                ) +
                ")";
    }
}
