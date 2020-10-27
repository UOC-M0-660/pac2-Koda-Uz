package edu.uoc.pac2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A book Model representing a piece of content.
 */
@Entity(tableName = "book")
data class Book(
        @PrimaryKey(autoGenerate = true)
        val uid: Int? = null,
        val title: String? = null,
        val author: String? = null,
        val description: String? = null,
        val publicationDate: String? = null,
        val urlImage: String? = null
)