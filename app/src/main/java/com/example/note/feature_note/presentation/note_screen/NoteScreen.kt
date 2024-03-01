package com.example.note.feature_note.presentation.note_screen

import android.app.ActionBar
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.note.feature_note.presentation.add_edit_note_screen.AddEditNoteScreen
import com.example.note.feature_note.presentation.note_screen.components.NoteItem
import com.example.note.feature_note.presentation.note_screen.components.OrderSection
import com.example.note.ui.Screen
import kotlinx.coroutines.launch

@Composable
fun NoteScreen(
    navController: NavController,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                          navController.navigate(Screen.AddEditNoteScreen.route)
                }, backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
            }
        }, scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Your Note", style = MaterialTheme.typography.h4)
                IconButton(onClick = { viewModel.onEvent(NoteEvent.ToggleOrderSection) }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Sort")
                }
            }
            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    noteOrder = state.noteOrder,
                    onOrderChange = {
                        viewModel.onEvent(NoteEvent.Order(it))
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.notes) { note ->
                    NoteItem(note = note, modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                                   navController.navigate(
                                       Screen.AddEditNoteScreen.route
                                               +"?noteId=${note.uid}&noteColor=${note.color}"
                                   )
                        }, onDeleteClick = {
                        viewModel.onEvent(NoteEvent.DeleteNote(note))
                        scope.launch {
                            val result = scaffoldState.snackbarHostState.showSnackbar(
                                message = "Note Deleted",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.onEvent(NoteEvent.RestoreNote)
                            }
                        }
                    }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        it
    }
}

@Preview
@Composable
fun NoteScreenPreview(){
    //NoteScreen(navController = )
}
