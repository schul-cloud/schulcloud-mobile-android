@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import androidx.lifecycle.*


// Construction
fun <T> T?.asLiveData(): LiveData<T> = liveDataOf(this)

fun <T> liveDataOf(initialValue: T? = null): LiveData<T> {
    return MutableLiveData<T>().apply { value = initialValue }
}

fun <T> mutableLiveDataOf(initialValue: T? = null): MutableLiveData<T> {
    return MutableLiveData<T>().apply { value = initialValue }
}

fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) { result.value = it }
    return result
}


// Mapping
fun <T, R> LiveData<T>.map(func: (T) -> R): LiveData<R> = Transformations.map(this, func)

fun <T, R> LiveData<T>.switchMap(func: (T) -> LiveData<R>): LiveData<R> {
    return Transformations.switchMap(this, func)
}

fun <T, R : Any?> LiveData<T>.switchMapNullable(func: (T) -> LiveData<R>?): LiveData<R> {
    val result = MediatorLiveData<R>()
    var source: LiveData<R>? = null
    result.addSource(this) { value ->
        val newLiveData = func(value) ?: liveDataOf()
        if (source == newLiveData)
            return@addSource

        source?.also { result.removeSource<R>(it) }
        source = newLiveData
        source?.also { source ->
            result.addSource(source) { result.value = it }
        }
    }
    return result
}


// Combination
fun <T> LiveData<T>.zip(other: LiveData<T>): LiveData<T> {
    val result = MediatorLiveData<T>()
    val observer = Observer<T> {
        result.value = it
    }
    result.addSource(this, observer)
    result.addSource(other, observer)
    return result
}

fun <T> zipLater(): Pair<LiveData<T>, (LiveData<T>) -> Unit> {
    val result = MediatorLiveData<T>()
    val addFunc: (LiveData<T>) -> Unit = { source ->
        result.addSource(source) { result.value = it }
    }
    return result to addFunc
}

fun <T> switch(): Pair<LiveData<T>, (LiveData<T>) -> Unit> {
    val result = MediatorLiveData<T>()
    val sources = mutableListOf<LiveData<T>>()
    val addFunc: (LiveData<T>) -> Unit = { source ->
        for (oldSource in sources)
            result.removeSource(oldSource)
        sources.clear()

        result.addSource(source) { result.value = it }
        sources += source
    }
    return result to addFunc
}

inline fun <reified T1 : Any, reified T2 : Any> LiveData<T1>.combineLatest(other: LiveData<T2>): LiveData<Pair<T1, T2>> {
    val result = object : MediatorLiveData<Pair<T1, T2>>() {
        var v1: T1? = null
        var v1Set = false
        var v2: T2? = null
        var v2Set = false

        @Suppress("NAME_SHADOWING")
        fun update() {
            if (!v1Set || !v2Set)
                return
            value = v1 as T1 to v2 as T2
        }
    }

    result.addSource(this) {
        result.v1 = it
        result.v1Set = true
        result.update()
    }
    result.addSource(other) {
        result.v2 = it
        result.v2Set = true
        result.update()
    }
    return result
}

inline fun <reified T1 : Any, reified T2> LiveData<T1>.combineLatestNullable(other: LiveData<T2?>): LiveData<Pair<T1, T2?>> {
    val result = object : MediatorLiveData<Pair<T1, T2?>>() {
        var v1: T1? = null
        var v1Set = false
        var v2: T2? = null
        var v2Set = false

        @Suppress("NAME_SHADOWING")
        fun update() {
            if (!v1Set || !v2Set)
                return
            value = v1 as T1 to v2
        }
    }

    result.addSource(this) {
        result.v1 = it
        result.v1Set = true
        result.update()
    }
    result.addSource(other) {
        result.v2 = it
        result.v2Set = true
        result.update()
    }
    return result
}

inline fun <reified T1, reified T2> LiveData<T1>.combineLatestBothNullable(other: LiveData<T2?>): LiveData<Pair<T1?, T2?>> {
    val result = object : MediatorLiveData<Pair<T1?, T2?>>() {
        var v1: T1? = null
        var v1Set = false
        var v2: T2? = null
        var v2Set = false

        fun update() {
            if (!v1Set || !v2Set)
                return
            value = v1 to v2
        }
    }

    result.addSource(this) {
        result.v1 = it
        result.v1Set = true
        result.update()
    }
    result.addSource(other) {
        result.v2 = it
        result.v2Set = true
        result.update()
    }
    return result
}

inline fun <reified T1, reified T2, reified T3> LiveData<T1>.combineLatest(
    other1: LiveData<T2>,
    other2: LiveData<T3>
): LiveData<Triple<T1, T2, T3>> {
    val result = object : MediatorLiveData<Triple<T1, T2, T3>>() {
        var v1: T1? = null
        var v1Set = false
        var v2: T2? = null
        var v2Set = false
        var v3: T3? = null
        var v3Set = false

        @Suppress("NAME_SHADOWING")
        fun update() {
            if (!v1Set || !v2Set || !v3Set)
                return
            value = Triple(v1 as T1, v2 as T2, v3 as T3)
        }
    }

    result.addSource(this) {
        result.v1 = it
        result.v1Set = true
        result.update()
    }
    result.addSource(other1) {
        result.v2 = it
        result.v2Set = true
        result.update()
    }
    result.addSource(other2) {
        result.v3 = it
        result.v3Set = true
        result.update()
    }
    return result
}


// Filtering
fun <T> LiveData<T>.first(count: Int = 1): LiveData<T> {
    var iteration = 0
    val result = MediatorLiveData<T>()
    result.addSource(this) {
        if (iteration < count) {
            result.value = it
            iteration++
        }
        if (iteration == count - 1)
            result.removeSource(this)
    }
    return result
}

inline fun <reified T> LiveData<T>.filter(crossinline predicate: (T) -> Boolean): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) {
        if (predicate(it))
            result.value = it
    }
    return result
}

inline fun <reified T> LiveData<T?>.filterNotNull(): LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) {
        if (it != null)
            result.value = it
    }
    return result
}
