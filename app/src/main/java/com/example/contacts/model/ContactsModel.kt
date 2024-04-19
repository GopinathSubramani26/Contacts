package com.example.contacts.model

data class ContactsModel(
    val info: Info,
    val results: List<Result>
)

data class Id(
    val name: String,
    val value: String
)

data class Info(
    val page: Int,
    val results: Int,
    val seed: String,
    val version: String
)

data class Name(
    val first: String,
    val last: String,
    val title: String
)

data class Picture(
    val large: String,
    val medium: String,
    val thumbnail: String
)

data class Result(
    val cell: String,
    val email: String,
    val gender: String,
    val id: Id,
    val name: Name,
    val phone: String,
    val picture: Picture
)

data class ContactsRandom(
    val id: Long?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val pictureMedium: String?
)


data class ContactsPhone(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String,
)


