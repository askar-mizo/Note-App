package com.example.note.feature_note.presentation.note_screen

import com.example.note.feature_note.domain.model.Note
import com.example.note.feature_note.presentation.util.NoteOrder
import com.example.note.feature_note.presentation.util.OrderType

data class NoteState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)
