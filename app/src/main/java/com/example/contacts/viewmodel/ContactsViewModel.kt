package com.example.contacts.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts.model.ContactsPhone
import com.example.contacts.model.ContactsRandom
import com.example.contacts.repository.ContactsRepository_Impl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class ContactsState {
    object Loading : ContactsState()
    object Success : ContactsState()
    data class Error(val message: String) : ContactsState()
    object None : ContactsState()
}

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: ContactsRepository_Impl,
    private val context: Application
) : ViewModel() {

    companion object {
        private const val TAG = "ContactsViewModel"
    }

    val randomContactsState = mutableStateOf<ContactsState>(ContactsState.None)
    val phoneContactsState = mutableStateOf<ContactsState>(ContactsState.None)

    val contactListRandom = mutableStateOf<List<ContactsRandom>>(emptyList())
    val contactListPhone = mutableStateOf<List<ContactsPhone>>(emptyList())

    fun fetchContactsRandomNetwork(){
        randomContactsState.value = ContactsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getContacts()
                    .distinctUntilChanged()
                    .collect { contactsModel -> }
                randomContactsState.value = ContactsState.Success

            } catch (e: Exception) {
                Log.e(TAG, "Fetch Random Contacts Failed: ${e.message}")
                randomContactsState.value = ContactsState.Error("Failed to fetch random contacts: ${e.message}")
            }
        }

    }

    fun fetchContactsRandom() {
        randomContactsState.value = ContactsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getContactsOffline()
                    .distinctUntilChanged()
                    .collect { contacts ->
                        val contactsBasicInfoList = contacts.map { contact ->
                            ContactsRandom(
                                id = contact.id,
                                firstName = contact.firstName,
                                lastName = contact.lastName,
                                email = contact.email,
                                phone = contact.phone,
                                pictureMedium = contact.pictureMedium
                            )
                        }
                        contactListRandom.value = contactsBasicInfoList
                        randomContactsState.value = ContactsState.Success
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Fetch Random Contacts Failed: ${e.message}")
                randomContactsState.value = ContactsState.Error("Failed to fetch random contacts: ${e.message}")
            }
        }
    }

    fun fetchContactsPhone() {
        phoneContactsState.value = ContactsState.Loading
        viewModelScope.launch {
            try {
                val contacts = withContext(Dispatchers.IO) {
                    retrieveContactsFromProvider()
                }
                contactListPhone.value = contacts
                phoneContactsState.value = ContactsState.Success
            } catch (e: Exception) {
                Log.e(TAG, "Fetch Phone Contacts Failed: ${e.message}")
                phoneContactsState.value = ContactsState.Error("Failed to fetch phone contacts: ${e.message}")
            }
        }
    }

    private suspend fun retrieveContactsFromProvider(): List<ContactsPhone> {
        return withContext(Dispatchers.IO) {
            val contactsList = mutableListOf<ContactsPhone>()
            val contentResolver: ContentResolver = context.contentResolver
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            cursor?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id: String =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name: String? =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    if (!name.isNullOrEmpty()) {
                        contactsList.add(
                            ContactsPhone(
                             id =   id.toLong(),
                                name=  name,
                              phoneNumber= " ",
                              email= " "
                            )
                        )
                    }
                }
            }
            cursor?.close()
            contactsList
        }
    }
}
