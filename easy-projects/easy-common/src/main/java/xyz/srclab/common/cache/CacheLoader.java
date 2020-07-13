package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Equal;
import xyz.srclab.common.base.Hash;
import xyz.srclab.common.base.ToString;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;

/**
 * @author sunqian
 */
public interface CacheLoader<K, V> {

    @Nullable
    default Result<V> load(K key) {
        return Result.of(simplyLoadValue(key));
    }

    @Nullable
    V simplyLoadValue(K key);

    interface Result<V> {

        static <V> Builder<V> newBuilder() {
            return new Builder<>();
        }

        static <V> Result<V> of(@Nullable V value) {
            return newBuilder()
                    .value(value)
                    .build();
        }

        boolean needCache();

        @Nullable
        V value();

        @Nullable
        CacheExpiry expiry();

        class Builder<V> extends BaseProductCachingBuilder<Result<V>> {

            private boolean needCache = true;
            private @Nullable V value;
            private @Nullable CacheExpiry expiry;

            public <V1 extends V> Builder<V1> value(boolean needCache) {
                this.needCache = needCache;
                this.updateState();
                return Cast.as(this);
            }

            public <V1 extends V> Builder<V1> value(@Nullable V1 value) {
                this.value = value;
                this.updateState();
                return Cast.as(this);
            }

            public <V1 extends V> Builder<V1> expiry(CacheExpiry expiry) {
                this.expiry = expiry;
                this.updateState();
                return Cast.as(this);
            }

            public <V1 extends V> Result<V1> build() {
                return Cast.as(buildCaching());
            }

            @Override
            protected Result<V> buildNew() {
                return new Result<V>() {

                    private final boolean needCache = Builder.this.needCache;
                    private final @Nullable V value = Builder.this.value;
                    private final @Nullable CacheExpiry expiry = Builder.this.expiry;

                    @Override
                    public boolean needCache() {
                        return needCache;
                    }

                    @Nullable
                    @Override
                    public V value() {
                        return value;
                    }

                    @Nullable
                    @Override
                    public CacheExpiry expiry() {
                        return expiry;
                    }

                    @Override
                    public int hashCode() {
                        return Hash.hash(value);
                    }

                    @Override
                    public boolean equals(@Nullable Object obj) {
                        if (obj == null || !getClass().equals(obj.getClass())) {
                            return false;
                        }
                        Result<?> that = (Result<?>) obj;
                        return needCache == that.needCache()
                                && Equal.equals(value, that.value())
                                && Equal.equals(expiry, that.expiry());
                    }

                    @Override
                    public String toString() {
                        return ToString.toString(value)
                                + "(needCache: " + needCache
                                + ", expiry: " + expiry
                                + ")";
                    }
                };
            }
        }
    }
}
