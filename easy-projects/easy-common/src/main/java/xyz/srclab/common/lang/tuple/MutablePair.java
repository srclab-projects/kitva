package xyz.srclab.common.lang.tuple;

import xyz.srclab.annotation.Nullable;

public interface MutablePair<A, B> extends Pair<A, B>, MutableTuple {

    static <A, B> MutablePair<A, B> create() {
        return Tuple0.newMutablePair();
    }

    static <A, B> MutablePair<A, B> of(@Nullable A first, @Nullable B second) {
        return Tuple0.newMutablePair(first, second);
    }

    void first(@Nullable A value);

    void second(@Nullable B value);
}
