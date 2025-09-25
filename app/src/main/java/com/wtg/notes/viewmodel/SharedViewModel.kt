package com.wtg.notes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wtg.notes.data.Repository
import com.wtg.notes.model.NoteModel
import com.wtg.notes.utils.IReadDataStore
import com.wtg.notes.utils.IWriteDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repo: Repository,
    private val writeStore: IWriteDataStore,
    val readStore: IReadDataStore
) : ViewModel() {
    // Set Dark Mode
    fun setDarkMode(isDarkMode: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        writeStore.setDarkMode(isDarkMode)
    }

    // Set Ads Mode
    fun setAdsMode(enableAds: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        writeStore.setAdsMode(enableAds)
    }

    fun setTypeView(isSwitched: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        writeStore.setSwitchView(isSwitched)
    }

    // Hide Stepper
    fun setStepper(hideStepper: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        writeStore.setStepper(hideStepper)
    }

    // Notes
    //private val _notes = MutableLiveData<List<Note>>()
    //val notes: LiveData<List<Note>> = _notes

    //Get last id from note inserted
    val idLastRow = MutableLiveData<Int>()

    // get all notes from db
    fun getAllNotes() = repo.getAllNotes().asLiveData()

    // get all notes -> filter: createdDate
    fun getAllNotesByCreatedDate() = repo.getAllNotesByCreatedDate().asLiveData()

    //Add new note
    fun addNote(note: NoteModel) = viewModelScope.launch {
        val id = repo.upsertNote(note.apply {
            modified = System.currentTimeMillis()
        })
        idLastRow.postValue(id.toInt())
    }

    // update or undelete notes
    fun updateNote(note: NoteModel) = viewModelScope.launch {
        repo.upsertNote(note.apply {
            modified = System.currentTimeMillis()
        })
    }

    // delete note
    fun deleteNote(note: NoteModel) = viewModelScope.launch {
        repo.deleteNote(note)
    }

    //delete all notes
    fun deleteAllNotes() = viewModelScope.launch {
        repo.deleteAllNotes()
    }

    fun checkpointDB() = viewModelScope.launch { repo.checkpoint()}
}