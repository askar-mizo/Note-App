package com.example.note.feature_note.presentation.add_edit_note_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note.feature_note.domain.model.InvalidNoteException
import com.example.note.feature_note.domain.model.Note
import com.example.note.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _noteTitleState = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter title.. "
        )
    )
    val noteTitleState: State<NoteTextFieldState> = _noteTitleState

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter some Content.. "
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent
    private var currentId: Int? = null

    private val _noteColor = mutableStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if (noteId != -1){
                viewModelScope.launch {
                    noteUseCases.getSingleNoteUseCase(noteId)?.also {note ->
                        currentId = note.uid
                        _noteTitleState.value = noteTitleState.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteContent.value = noteContent.value.copy(
                            text = note.content,
                            isHintVisible = false
                        )
                        _noteColor.value = note.color
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteTitleState.value = _noteTitleState.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            _noteContent.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitleState.value = noteTitleState.value
                    .copy(
                        isHintVisible = !event.focusState.isFocused &&
                                noteTitleState.value.text.isBlank()
                    )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteTitleState.value = _noteTitleState.value.copy(text = event.value)
            }
            is AddEditNoteEvent.EnteredTitle ->
                _noteTitleState.value = _noteTitleState.value.copy(text = event.value)
            AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNoteUseCase(
                            Note(
                                title = noteTitleState.value.text,
                                content = noteContent.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                uid = currentId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't save Note"
                            )
                        )
                    }
                }
            }
        }

    }

    sealed class UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}