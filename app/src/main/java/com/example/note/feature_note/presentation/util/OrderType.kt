package com.example.note.feature_note.presentation.util

sealed class OrderType{
    object Ascending: OrderType()
    object Descending: OrderType()
}
