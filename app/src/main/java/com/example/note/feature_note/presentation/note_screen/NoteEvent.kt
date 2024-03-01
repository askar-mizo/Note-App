package com.example.note.feature_note.presentation.note_screen

import com.example.note.feature_note.domain.model.Note
import com.example.note.feature_note.presentation.util.NoteOrder

sealed class NoteEvent{
    data class Order(val noteOrder: NoteOrder): NoteEvent()
    data class DeleteNote(val note: Note): NoteEvent()
    object RestoreNote: NoteEvent()
    object ToggleOrderSection: NoteEvent()
}
