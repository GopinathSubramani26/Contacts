package com.example.contacts.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.contacts.R
import com.example.contacts.common.CommonButton
import com.example.contacts.common.CommonHeader
import com.example.contacts.common.CommonTextField
import com.example.contacts.ui.theme.mediumTextStyleSize18
import com.example.contacts.ui.theme.primaryTextColor
import com.example.contacts.ui.theme.white
import com.example.contacts.viewmodel.CreateContactsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateContact(navController: NavController, viewModel: CreateContactsViewModel) {

    val coroutineScope = rememberCoroutineScope()

    val contactId = navController.currentBackStackEntry?.arguments?.getLong("contactId")
    val contactName = navController.currentBackStackEntry?.arguments?.getString("source")?.trim()
    val tabState = navController.currentBackStackEntry?.arguments?.getInt("tab",0)

    Log.d("CreateContact", "contactId: $contactId")
    Log.d("CreateContact", "contactName: $contactName")
    Log.d("CreateContact", "tabState: $tabState")

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearAllData()
        }
    }

    LaunchedEffect(viewModel.updateCompleted.value) {
        if (viewModel.updateCompleted.value) {
            navController.popBackStack()
            viewModel.resetUpdateCompleted()
        }
    }

    when {
        contactId != null && contactName == "random" -> {
            LaunchedEffect(contactId) {
                viewModel.getContactById(contactId)
            }
        }
        contactId != null && contactName == "phone" -> {
            LaunchedEffect(contactId) {
                viewModel.retrieveContactsFromProviderById(contactId.toString())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CommonHeader(
            text = if (contactId != 0L) "Edit Contacts" else "Add Contacts",
            image = painterResource(id = R.drawable.left_arrow),
            onBackClick = {
                navController.popBackStack()
                viewModel.clearAllData()
            })

        CommonTextField(
            value = viewModel.firstName.value,
            onValueChange = { newText -> viewModel.firstName.value = newText },
            placeholder = if(contactName !="phone" && tabState == 0)"First Name" else "Name",
            textStyle = MaterialTheme.typography.mediumTextStyleSize18,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if(contactName != "phone" && tabState == 0){
            CommonTextField(
                value = viewModel.lastName.value,
                onValueChange = { newText -> viewModel.lastName.value = newText },
                placeholder = "Last Name",
                textStyle = MaterialTheme.typography.mediumTextStyleSize18,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        CommonTextField(
            value = viewModel.email.value,
            onValueChange = { newText -> viewModel.email.value = newText },
            placeholder = "Email",
            textStyle = MaterialTheme.typography.mediumTextStyleSize18,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CommonTextField(
            value = viewModel.phone.value,
            onValueChange = { newText -> viewModel.phone.value = newText },
            placeholder = "Phone",
            textStyle = MaterialTheme.typography.mediumTextStyleSize18,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.padding(vertical = 24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CommonButton(
                contentColor = white,
                backgroundColor = primaryTextColor,
                txt = if (contactId != 0L) "Update" else "Save",
                modifier = Modifier.weight(1f)
            ) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        if (contactId != null && contactId.toInt() != 0) {
                            if (contactName == "phone") {
                                viewModel.updateContactInProvider(contactId.toLong(), viewModel.firstName.value, viewModel.phone.value)
                            } else {
                                viewModel.updateContact()
                            }
                        } else {
                            if (tabState == 0) {
                                viewModel.insertContact()
                            } else {
                                viewModel.insertContactToPhone()
                            }
                        }
                    }
                }
            }
        }
    }
}

