package org.schulcloud.mobile.utils

import java.util.*


fun <T> List<T>.limit(limit: Int): List<T> {
    return subList(0, Math.min(limit, size))
}

fun <K, V> Map<out K?, V>.filterKeysNotNull(): Map<K, V> {
    val result = LinkedHashMap<K, V>()
    for ((key, value) in this)
        if (key != null)
            result[key] = value
    return result
}

fun <T> List<T>.move(sourceIndex: Int, targetIndex: Int) {
    if (sourceIndex <= targetIndex)
        Collections.rotate(subList(sourceIndex, targetIndex + 1), -1)
    else
        Collections.rotate(subList(targetIndex, sourceIndex + 1), 1)
}
