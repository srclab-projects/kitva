package xyz.srclab.common.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.cache.RemovalListener
import com.google.common.collect.MapMaker
import xyz.srclab.common.base.CachingProductBuilder
import xyz.srclab.common.base.Default
import xyz.srclab.common.base.asAny
import java.time.Duration
import com.github.benmanes.caffeine.cache.RemovalCause as caffeineRemovalCause
import com.google.common.cache.RemovalCause as guavaRemovalCause

/**
 * @see GuavaCache
 * @see GuavaLoadingCache
 * @see CaffeineCache
 * @see CaffeineLoadingCache
 * @see MapCache
 * @see ThreadLocalCache
 */
interface Cache<K : Any, V> {

    @Throws(NoSuchElementException::class)
    @JvmDefault
    fun get(key: K): V {
        val thisAs = this.asAny<Cache<K, Any>>()
        val value = thisAs.getOrElse(key, Default.ABSENT)
        if (value === Default.ABSENT) {
            throw NoSuchElementException(key.toString())
        }
        return value.asAny()
    }

    @JvmDefault
    fun getOrNull(key: K): V? {
        val thisAs = this.asAny<Cache<K, Any>>()
        val value = thisAs.getOrElse(key, Default.ABSENT)
        if (value === Default.ABSENT) {
            return null
        }
        return value.asAny()
    }

    fun getOrElse(key: K, defaultValue: V): V

    @JvmDefault
    fun getOrElse(key: K, defaultValue: (K) -> V): V {
        val thisAs = this.asAny<Cache<K, Any>>()
        val value = thisAs.getOrElse(key, Default.ABSENT)
        if (value === Default.ABSENT) {
            return defaultValue(key)
        }
        return value.asAny()
    }

    fun getOrLoad(key: K, loader: (K) -> V): V

    @JvmDefault
    fun getPresent(keys: Iterable<K>): Map<K, V> {
        val resultMap = mutableMapOf<K, V>()
        val thisAs = this.asAny<Cache<K, Any>>()
        for (key in keys) {
            val value = thisAs.getOrElse(key, Default.ABSENT)
            if (value !== Default.ABSENT) {
                resultMap[key] = value.asAny()
            }
        }
        return resultMap.toMap()
    }

    @JvmDefault
    fun getAll(keys: Iterable<K>, loader: (Iterable<K>) -> Map<K, V>): Map<K, V> {
        val resultMap = mutableMapOf<K, V>()
        val thisAs = this.asAny<Cache<K, Any>>()
        for (key in keys) {
            val value = thisAs.getOrElse(key, Default.ABSENT)
            if (value !== Default.ABSENT) {
                resultMap[key] = value.asAny()
            }
        }
        resultMap.putAll(loader(keys.minus(resultMap.keys)))
        return resultMap.toMap()
    }

    fun put(key: K, value: V)

    fun put(key: K, value: V, expirySeconds: Long)

    fun put(key: K, value: V, expiry: Duration)

    fun putAll(entries: Map<out K, V>)

    fun putAll(entries: Map<out K, V>, expirySeconds: Long)

    fun putAll(entries: Map<out K, V>, expiry: Duration)

    fun expiry(key: K, expirySeconds: Long)

    fun expiry(key: K, expirySeconds: Duration)

    fun expiryAll(keys: Iterable<K>, expirySeconds: Long)

    fun expiryAll(keys: Iterable<K>, expirySeconds: Duration)

    fun invalidate(key: K)

    fun invalidateAll(keys: Iterable<K>)

    fun invalidateAll()

    fun cleanUp()

    class Builder<K : Any, V> : CachingProductBuilder<Cache<K, V>>() {

        private var initialCapacity: Int? = null
        private var maxSize: Long? = null
        private var concurrencyLevel: Int? = null
        private var expireAfterAccess: Duration? = null
        private var expireAfterWrite: Duration? = null
        private var refreshAfterWrite: Duration? = null
        private var loader: ((K) -> V)? = null
        private var createListener: CacheCreateListener<in K, in V>? = null
        private var readListener: CacheReadListener<in K, in V>? = null
        private var updateListener: CacheUpdateListener<in K, in V>? = null
        private var removeListener: CacheRemoveListener<in K, in V>? = null
        private var useGuava = false

        fun initialCapacity(initialCapacity: Int): Builder<K, V> {
            this.initialCapacity = initialCapacity
            this.commitChange()
            return this
        }

        fun maxSize(maxSize: Long): Builder<K, V> {
            this.maxSize = maxSize
            this.commitChange()
            return this
        }

        fun concurrencyLevel(concurrencyLevel: Int): Builder<K, V> {
            this.concurrencyLevel = concurrencyLevel
            this.commitChange()
            return this
        }

        fun expireAfterAccess(expireAfterAccess: Duration): Builder<K, V> {
            this.expireAfterAccess = expireAfterAccess
            this.commitChange()
            return this
        }

        fun expireAfterWrite(expireAfterWrite: Duration): Builder<K, V> {
            this.expireAfterWrite = expireAfterWrite
            this.commitChange()
            return this
        }

        fun refreshAfterWrite(refreshAfterWrite: Duration): Builder<K, V> {
            this.refreshAfterWrite = refreshAfterWrite
            this.commitChange()
            return this
        }

        fun loader(loader: ((K) -> V)): Builder<K, V> {
            this.loader = loader
            this.commitChange()
            return this
        }

        fun createListener(createListener: CacheCreateListener<in K, in V>): Builder<K, V> {
            this.createListener = createListener
            this.commitChange()
            return this
        }

        fun readListener(readListener: CacheReadListener<in K, in V>): Builder<K, V> {
            this.readListener = readListener
            this.commitChange()
            return this
        }

        fun updateListener(updateListener: CacheUpdateListener<in K, in V>): Builder<K, V> {
            this.updateListener = updateListener
            this.commitChange()
            return this
        }

        fun removeListener(removeListener: CacheRemoveListener<in K, in V>): Builder<K, V> {
            this.removeListener = removeListener
            this.commitChange()
            return this
        }

        fun useGuava(useGuava: Boolean): Builder<K, V> {
            this.useGuava = useGuava
            this.commitChange()
            return this
        }

        override fun buildNew(): Cache<K, V> {
            val params = Params(
                initialCapacity,
                maxSize,
                concurrencyLevel,
                expireAfterAccess,
                expireAfterWrite,
                refreshAfterWrite,
                loader,
                createListener,
                readListener,
                updateListener,
                removeListener,
            )
            return if (useGuava) {
                buildGuavaCache(params)
            } else {
                buildCaffeineCache(params)
            }
        }

        private fun buildGuavaCache(params: Params<K, V>): Cache<K, V> {
            val guavaBuilder = com.google.common.cache.CacheBuilder.newBuilder()
            if (params.initialCapacity !== null) {
                guavaBuilder.initialCapacity(params.initialCapacity)
            }
            if (params.maxSize !== null) {
                guavaBuilder.maximumSize(params.maxSize)
            }
            if (params.concurrencyLevel !== null) {
                guavaBuilder.concurrencyLevel(params.concurrencyLevel)
            }
            if (params.expireAfterAccess !== null) {
                guavaBuilder.expireAfterAccess(params.expireAfterAccess)
            }
            if (params.expireAfterWrite !== null) {
                guavaBuilder.expireAfterWrite(params.expireAfterWrite)
            }
            if (params.refreshAfterWrite !== null) {
                guavaBuilder.refreshAfterWrite(params.refreshAfterWrite)
            }
            if (params.removeListener !== null) {
                guavaBuilder.removalListener(RemovalListener<K, V> { notification ->
                    val removeCause: CacheRemoveListener.Cause = when (notification.cause) {
                        guavaRemovalCause.EXPLICIT -> CacheRemoveListener.Cause.EXPLICIT
                        guavaRemovalCause.REPLACED -> CacheRemoveListener.Cause.REPLACED
                        guavaRemovalCause.COLLECTED -> CacheRemoveListener.Cause.COLLECTED
                        guavaRemovalCause.EXPIRED -> CacheRemoveListener.Cause.EXPIRED
                        guavaRemovalCause.SIZE -> CacheRemoveListener.Cause.SIZE
                    }
                    params.removeListener.afterRemove(notification.key, notification.value, removeCause)
                })
            }
            val loader = params.loader
            return if (loader === null) {
                val guavaCache = guavaBuilder.build<K, V>()
                GuavaCache(guavaCache)
            } else {
                val loadingGuavaCache =
                    guavaBuilder.build(object : com.google.common.cache.CacheLoader<K, V>() {
                        override fun load(key: K): V {
                            return loader(key)
                        }
                    })
                GuavaLoadingCache(loadingGuavaCache)
            }
        }

        private fun buildCaffeineCache(params: Params<K, V>): Cache<K, V> {
            val caffeineBuilder = Caffeine.newBuilder()
            if (params.initialCapacity !== null) {
                caffeineBuilder.initialCapacity(params.initialCapacity)
            }
            if (params.maxSize !== null) {
                caffeineBuilder.maximumSize(params.maxSize)
            }
            if (params.expireAfterAccess !== null) {
                caffeineBuilder.expireAfterAccess(params.expireAfterAccess)
            }
            if (params.expireAfterWrite !== null) {
                caffeineBuilder.expireAfterWrite(params.expireAfterWrite)
            }
            if (params.refreshAfterWrite !== null) {
                caffeineBuilder.refreshAfterWrite(params.refreshAfterWrite)
            }
            if (params.removeListener !== null) {
                caffeineBuilder.removalListener<K, V> { key, value, cause ->
                    val removeCause: CacheRemoveListener.Cause = when (cause) {
                        caffeineRemovalCause.EXPLICIT -> CacheRemoveListener.Cause.EXPLICIT
                        caffeineRemovalCause.REPLACED -> CacheRemoveListener.Cause.REPLACED
                        caffeineRemovalCause.COLLECTED -> CacheRemoveListener.Cause.COLLECTED
                        caffeineRemovalCause.EXPIRED -> CacheRemoveListener.Cause.EXPIRED
                        caffeineRemovalCause.SIZE -> CacheRemoveListener.Cause.SIZE
                    }
                    params.removeListener.afterRemove(key.asAny(), value.asAny(), removeCause)
                }
            }
            val loader = params.loader
            return if (loader === null) {
                val guavaCache = caffeineBuilder.build<K, V>()
                CaffeineCache(guavaCache)
            } else {
                val loadingGuavaCache = caffeineBuilder.build<K, V> { k -> loader(k) }
                CaffeineLoadingCache(loadingGuavaCache)
            }
        }

        private data class Params<K, V>(
            val initialCapacity: Int? = null,
            val maxSize: Long? = null,
            val concurrencyLevel: Int? = null,
            val expireAfterAccess: Duration? = null,
            val expireAfterWrite: Duration? = null,
            val refreshAfterWrite: Duration? = null,
            val loader: ((K) -> V)? = null,
            val createListener: CacheCreateListener<in K, in V>? = null,
            val readListener: CacheReadListener<in K, in V>? = null,
            val updateListener: CacheUpdateListener<in K, in V>? = null,
            val removeListener: CacheRemoveListener<in K, in V>? = null,
        )
    }

    companion object {

        @JvmStatic
        fun <K : Any, V> newBuilder(): Builder<K, V> {
            return Builder()
        }

        /**
         * Return a new fast Cache.
         *
         * Does not support to set expiry time, and if the value has been created, cannot update (put) it.
         */
        @JvmStatic
        fun <K : Any, V> newFastCache(): Cache<K, V> {
            return MapCache(MapMaker().concurrencyLevel(Default.concurrencyLevel).weakValues().makeMap())
        }
    }
}

interface CacheCreateListener<K, V> {
    fun beforeCreate(key: K)
    fun afterCreate(key: K, value: V)
}

interface CacheReadListener<K, V> {
    fun beforeRead(key: K)
    fun onHit(key: K, value: V)
    fun onMiss(key: K)
}

interface CacheUpdateListener<K, V> {
    fun beforeUpdate(key: K, oldValue: V)
    fun afterUpdate(key: K, oldValue: V, newValue: V)
}

interface CacheRemoveListener<K, V> {
    fun beforeRemove(key: K, value: V, cause: Cause)
    fun afterRemove(key: K, value: V, cause: Cause)

    enum class Cause {
        /**
         * The entry was manually removed by the user. This can result from the user invoking any of the
         * following methods on the cache or map view.
         */
        EXPLICIT {
            override val isEvicted: Boolean
                get() {
                    return false
                }
        },

        /**
         * The entry itself was not actually removed, but its value was replaced by the user. This can
         * result from the user invoking any of the following methods on the cache or map view.
         */
        REPLACED {
            override val isEvicted: Boolean
                get() {
                    return false
                }
        },

        /**
         * The entry was removed automatically because its key or value was garbage-collected.
         */
        COLLECTED {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        },

        /**
         * The entry's expiration timestamp has passed.
         */
        EXPIRED {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        },

        /**
         * The entry was evicted due to size constraints.
         */
        SIZE {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        };

        /**
         * Returns `true` if there was an automatic removal due to eviction (the cause is neither
         * [EXPLICIT] nor [REPLACED]).
         *
         * @return if the entry was automatically removed due to eviction
         */
        abstract val isEvicted: Boolean
    }
}