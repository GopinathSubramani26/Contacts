package com.example.contacts

sealed class Screen(val route: String) {
    object ContactScreen : Screen("ContactScreen")
    object CreateContactScreen : Screen("CreateContact")
}
