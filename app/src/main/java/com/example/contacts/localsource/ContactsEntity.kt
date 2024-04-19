package com.example.contacts.localsource

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val infoSeed: String? = null,
    val infoResults: Int? = null,
    val infoPage: Int? = null,
    val infoVersion: String? = null,
    val gender: String? = null,
    val nameTitle: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val cell: String? = null,
    val idName: String? = null,
    val idValue: String? = null,
    val pictureLarge: String? = null,
    val pictureMedium: String? = null,
    val pictureThumbnail: String? = null
)
