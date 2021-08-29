package com.dreamdev.testtask.enums

sealed class ItemsChangesAction<out T: Any> {
    data class Addition<out T : Any>(val item: T) : ItemsChangesAction<T>()
    data class Removing<out T : Any>(val item: T) : ItemsChangesAction<T>()
}