package com.example.contacts.repository

import android.util.Log
import com.example.contacts.localsource.ContactsDao
import com.example.contacts.localsource.ContactsEntity
import com.example.contacts.model.ContactsModel
import com.example.contacts.networksource.ContactsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsRepository_Impl @Inject constructor(
    private val contactsService: ContactsService,
    private val contactsDao: ContactsDao
) : ContactsRepository {

    companion object {
        const val TAG = "ContactsRepository_Impl"
    }

    override suspend fun getContacts(): Flow<ContactsModel> = flow {
        try {
                val response = contactsService.getContacts()
                if (response.isSuccessful) {
                    response.body()?.let { contactsModel ->
                        emit(contactsModel)
                        Log.d(TAG, "Random Contacts Online: $contactsModel")

                        val contactsEntities = mutableListOf<ContactsEntity>()
                        for (result in contactsModel.results) {
                            val id = result.id
                            val name = result.name
                            val picture = result.picture

                            val contactsEntity = ContactsEntity(
                                infoSeed = contactsModel.info.seed,
                                infoResults = contactsModel.info.results,
                                infoPage = contactsModel.info.page,
                                infoVersion = contactsModel.info.version,
                                gender = result.gender,
                                nameTitle = name.title,
                                firstName = name.first,
                                lastName = name.last,
                                email = result.email,
                                phone = result.phone,
                                cell = result.cell,
                                idName = id.name,
                                idValue = id.value,
                                pictureLarge = picture.large,
                                pictureMedium = picture.medium,
                                pictureThumbnail = picture.thumbnail
                            )
                            contactsEntities.add(contactsEntity)
                        }

                        withContext(Dispatchers.IO) {
                            contactsDao.insertContacts(contactsEntities)
                        }
                    }
                }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getContactsOffline(): Flow<List<ContactsEntity>> = flow {
        try {
            val contactsList = withContext(Dispatchers.IO) {
                contactsDao.getContacts()
            }
            Log.d(TAG, "Random Contacts Offline: $contactsList")
            emit(contactsList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getContactById(contactId: Long): Flow<ContactsEntity?> = flow{
        try {
            val contactById = withContext(Dispatchers.IO) {
                contactsDao.getContactById(contactId)
            }
            Log.d(TAG, "Get ContactById: $contactById")
            emit(contactById)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateContact(contact: ContactsEntity): Flow<ContactsEntity> = flow {
        try {
            val existingContact = contactsDao.getContactById(contact.id ?: return@flow) ?: return@flow

            val updatedContact = existingContact.copy(
                firstName = contact.firstName ?: existingContact.firstName,
                lastName = contact.lastName ?: existingContact.lastName,
                email = contact.email ?: existingContact.email,
                phone = contact.phone ?: existingContact.phone
            )
            contactsDao.updateContact(updatedContact)
            emit(updatedContact)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun insertContact(contact: ContactsEntity): Flow<ContactsEntity> = flow {
        try {
            contactsDao.insertContact(contact)
            emit(contact)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

