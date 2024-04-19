package com.example.contacts.repository

import com.example.contacts.localsource.ContactsEntity
import com.example.contacts.model.ContactsModel
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {

    suspend fun getContacts(): Flow<ContactsModel>

    suspend fun getContactsOffline(): Flow<List<ContactsEntity>>

    suspend fun getContactById(contactId: Long): Flow<ContactsEntity?>

    suspend fun updateContact(contact:ContactsEntity): Flow<ContactsEntity>

    suspend fun insertContact(contact: ContactsEntity): Flow<ContactsEntity>

}

