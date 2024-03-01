package com.example.note.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.note.feature_note.domain.model.Note.Companion.TABLE_NAME
import com.example.note.ui.theme.*

@Entity(tableName = TABLE_NAME)
data class Note(
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int
){
    companion object{
        const val TABLE_NAME = "note_table"
        val noteColors = listOf(RedOrange, Yellow, Rose, Violet, Green, Purple200, Teal200, Purple500)
    }
}
class InvalidNoteException(message: String): java.lang.Exception(message) {

}