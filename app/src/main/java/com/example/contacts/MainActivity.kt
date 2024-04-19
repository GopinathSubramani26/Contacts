package com.example.contacts

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contacts.ui.theme.ContactsTheme
import com.example.contacts.view.ContactScreen
import com.example.contacts.view.CreateContact
import com.example.contacts.viewmodel.ContactsViewModel
import com.example.contacts.viewmodel.CreateContactsViewModel
import dagger.hilt.android.AndroidEntryPoint

const val READ_CONTACTS_PERMISSION = android.Manifest.permission.READ_CONTACTS
const val WRITE_CONTACTS_PERMISSION = android.Manifest.permission.WRITE_CONTACTS

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contactsViewModel: ContactsViewModel by viewModels()
        val createContactsViewModel: CreateContactsViewModel by viewModels()

        val permissionsToRequest = arrayOf(
            WRITE_CONTACTS_PERMISSION,
            READ_CONTACTS_PERMISSION
        )

        val multiplePermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.all { it.value }) {
                    contactsViewModel.fetchContactsPhone()
                } else {
                    val deniedPermissions = permissions.filter { !it.value }
                    if (deniedPermissions.any { !shouldShowRequestPermissionRationale(it.key) }) {
                        showAppSettingsDialog()
                    } else {
                        Toast.makeText(this, "Contacts permissions are required.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }

        setContent {
            ContactsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController, startDestination = Screen.ContactScreen.route) {

                        composable(Screen.ContactScreen.route) {
                            ContactScreen(
                                navController = navController,
                                viewModel = contactsViewModel
                            )
                        }

                        composable(
                            "${Screen.CreateContactScreen.route}/{contactId}?source={source}",
                            arguments = listOf(
                                navArgument("contactId") { type = NavType.LongType },
                                navArgument("source") { type = NavType.StringType })
                        ) { backStackEntry ->
                            CreateContact(navController, viewModel = createContactsViewModel)
                        }

                        composable(
                            "${Screen.CreateContactScreen.route}/{tab}",
                            arguments = listOf(
                                navArgument("tab") { type = NavType.IntType },)
                        ) { backStackEntry ->
                            CreateContact(navController, viewModel = createContactsViewModel)
                        }
                    }
                }
            }
        }

        requestPermissions(permissionsToRequest, multiplePermissionResultLauncher)

    }


    private fun requestPermissions(
        permissions: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        val permissionsToRequest = permissions.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (permissionsToRequest.isNotEmpty()) {
            launcher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun showAppSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions required")
            .setMessage("Please grant access to contacts in App Settings to continue.")
            .setPositiveButton("Go to Settings") { _, _ ->
                navigateToAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}


