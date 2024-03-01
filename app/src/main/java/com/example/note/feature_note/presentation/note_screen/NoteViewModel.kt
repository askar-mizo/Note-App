package com.example.note.feature_note.presentation.note_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note.feature_note.domain.model.Note
import com.example.note.feature_note.domain.use_case.NoteUseCases
import com.example.note.feature_note.presentation.util.NoteOrder
import com.example.note.feature_note.presentation.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteUseCase: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf(NoteState())
    val state: State<NoteState> = _state

    private var recentlyDeletedNote: Note? = null
    private var noteJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCase.deleteNoteUseCase(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NoteEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class
                    && state.value.noteOrder.orderType == event.noteOrder.orderType
                )return
                getNotes(event.noteOrder)
            }
            is NoteEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCase.addNoteUseCase(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NoteEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        noteJob?.cancel()
        noteJob = noteUseCase.getNoteUseCase(noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }.launchIn(viewModelScope)
    }
}