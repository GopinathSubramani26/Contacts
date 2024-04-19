package com.example.contacts.viewmodel

import android.app.Application
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts.localsource.ContactsEntity
import com.example.contacts.model.ContactsPhone
import com.example.contacts.model.ContactsRandom
import com.example.contacts.repository.ContactsRepository_Impl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateContactsViewModel @Inject constructor(
    private val repository: ContactsRepository_Impl,
    private val context: Application
) : ViewModel() {

    companion object {
        const val TAG = "CreateContactsViewModel"
    }

    val contactById = mutableStateOf<ContactsRandom?>(null)

    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var email = mutableStateOf("")
    var phone = mutableStateOf("")

    val updateCompleted = mutableStateOf(false)

    fun resetUpdateCompleted() {
        updateCompleted.value = false
    }

    fun clearAllData(){
        firstName.value = ""
        lastName.value = ""
        email.value = ""
        phone.value = ""
    }
    fun getContactById(contactId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getContactById(contactId).collect { contactEntity ->
                    contactById.value = contactEntity?.let { mapContactsEntityToRandom(it) }
                    firstName.value = contactEntity?.firstName ?: ""
                    lastName.value = contactEntity?.lastName ?: ""
                    email.value = contactEntity?.email ?: ""
                    phone.value = contactEntity?.phone ?: ""
                    Log.d(TAG, "$contactById")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting contact by ID", e)
            }
        }
    }

    fun updateContact() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contactId = contactById.value?.id ?: return@launch
                val updatedContact = ContactsEntity(
                    id = contactId,
                    firstName = firstName.value.takeIf { it.isNotEmpty() },
                    lastName = lastName.value.takeIf { it.isNotEmpty() },
                    email = email.value.takeIf { it.isNotEmpty() },
                    phone = phone.value.takeIf { it.isNotEmpty() }
                )
                repository.updateContact(updatedContact).collect { updatedContact ->
                    updateCompleted.value = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating contact", e)
            }
        }
    }

    fun insertContact() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newContact = ContactsEntity(
                    firstName = firstName.value.takeIf { it.isNotEmpty() },
                    lastName = lastName.value.takeIf { it.isNotEmpty() },
                    email = email.value.takeIf { it.isNotEmpty() },
                    phone = phone.value.takeIf { it.isNotEmpty() }
                )
                repository.insertContact(newContact).collect { insertedContact ->
                    updateCompleted.value = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting contact", e)
            }
        }
    }

    private fun mapContactsEntityToRandom(entity: ContactsEntity): ContactsRandom {
        return ContactsRandom(
            id = entity.id,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            phone = entity.phone,
            pictureMedium = entity.pictureMedium
        )
    }

    suspend fun retrieveContactsFromProviderById(contactId: String): ContactsPhone? {
        return withContext(Dispatchers.IO) {
            val contentResolver: ContentResolver = context.contentResolver
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                "${ContactsContract.Contacts._ID} = ?",
                arrayOf(contactId),
                null
            )
            var contact: ContactsPhone? = null
            cursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val name: String? =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber: String? = getPhoneNumber(contentResolver, contactId)
                    val emaill: String? = getEmail(contentResolver, contactId)

                    if (!name.isNullOrEmpty()) {
                        firstName.value = name ?: ""
                        email.value = emaill ?: ""
                        phone.value = phoneNumber?: ""

                        contact = ContactsPhone(
                          id =   contactId.toLong(),
                            name = name,
                            phoneNumber=phoneNumber ?: "",
                           email= emaill ?: ""
                        )
                    }
                }
            }

            Log.d(TAG,"$contact")
            cursor?.close()
            contact
        }
    }

    private suspend fun getPhoneNumber(contentResolver: ContentResolver, contactId: String): String? {
        return withContext(Dispatchers.IO) {
            val phoneCursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(contactId),
                null
            )
            var phoneNumber: String? = null
            phoneCursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    phoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                }
            }
            phoneCursor?.close()
            phoneNumber
        }
    }

    private suspend fun getEmail(contentResolver: ContentResolver, contactId: String): String? {
        return withContext(Dispatchers.IO) {
            val emailCursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf(contactId),
                null
            )
            var email: String? = null
            emailCursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                }
            }
            emailCursor?.close()
            email
        }
    }

    suspend fun updateContactInProvider(contactId: Long, name: String?, phone: String?): Boolean {
        return withContext(Dispatchers.IO) {
            val contentResolver: ContentResolver = context.contentResolver

            val nameValue = ContentValues().apply {
                name?.let { put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, it) }
            }

            val nameSelection = "${ContactsContract.Data.CONTACT_ID} = ? AND " +
                    "${ContactsContract.Data.MIMETYPE} IN (?, ?)"
            val nameAndSelectionArgs = arrayOf(
                contactId.toString(),
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
            )

            val updatedNameRow = contentResolver.update(
                ContactsContract.Data.CONTENT_URI,
                nameValue,
                nameSelection,
                nameAndSelectionArgs
            )

            Log.d("UpdateContact", "Updated Name: $updatedNameRow")

            val phoneValues = ContentValues().apply {
                phone?.let { put(ContactsContract.CommonDataKinds.Phone.NUMBER, it) }
            }

            val phoneSelection = "${ContactsContract.Data.CONTACT_ID} = ? AND " +
                    "${ContactsContract.Data.MIMETYPE} = ?"
            val phoneSelectionArgs = arrayOf(
                contactId.toString(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )

            val updatedPhoneRows = contentResolver.update(
                ContactsContract.Data.CONTENT_URI,
                phoneValues,
                phoneSelection,
                phoneSelectionArgs
            )
            updateCompleted.value = true

            updatedNameRow > 0 && updatedPhoneRows > 0

        }
    }


    fun insertContactToPhone() {
        viewModelScope.launch(Dispatchers.IO) {
            val ops = ArrayList<ContentProviderOperation>()

            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "${firstName.value} ${lastName.value}")
                    .build()
            )

            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.value)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build()
            )

            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email.value)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build()
            )

            try {
                val results = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                Log.d(TAG, "Contacts added successfully: $results")
                updateCompleted.value = true

            } catch (e: Exception) {
                Log.e(TAG, "Error inserting contact", e)
                updateCompleted.value = false
            }
        }
    }
}
