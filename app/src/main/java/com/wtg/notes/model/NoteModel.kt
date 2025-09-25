package com.wtg.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note")//room ; to create sqlite objects
data class NoteModel(
    @PrimaryKey(autoGenerate = true)
    var note_id: Int = 0,
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "content")
    var content: String = "",
    @ColumnInfo(name = "category")
    var category: Int = -1,
    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false,
    @ColumnInfo(name = "password")
    var password: String = "",
    @ColumnInfo(name = "color")
    var color: Int = 0,
    @ColumnInfo(name = "created")
    var created: Long = 0,
    @ColumnInfo(name = "modified")
    var modified: Long = 0,
) : Serializable
