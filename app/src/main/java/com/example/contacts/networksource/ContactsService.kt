package com.example.contacts.networksource

import com.example.contacts.model.ContactsModel
import retrofit2.Response
import retrofit2.http.GET

interface ContactsService {

    @GET("?results=25&inc=gender,name,picture,phone,cell,id,email")
    suspend fun getContacts():Response<ContactsModel>

}