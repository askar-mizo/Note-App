package com.example.note.di

import android.app.Application
import androidx.room.Room
import com.example.note.feature_note.data.data_source.NoteDatabase
import com.example.note.feature_note.data.data_source.NoteDatabase.Companion.DATABASE_NAME
import com.example.note.feature_note.data.repository.NoteRepositoryImpl
import com.example.note.feature_note.domain.repository.NoteRepository
import com.example.note.feature_note.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNoteUseCase = GetNoteUseCase(repository),
            deleteNoteUseCase = DeleteNoteUseCase(repository),
            addNoteUseCase = AddNoteUseCase(repository),
            getSingleNoteUseCase = GetSingleNoteUseCase(repository)
        )
    }
}