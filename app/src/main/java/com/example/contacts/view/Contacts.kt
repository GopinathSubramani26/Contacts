package com.example.contacts.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.contacts.R
import com.example.contacts.Screen
import com.example.contacts.common.CommonText
import com.example.contacts.model.ContactsPhone
import com.example.contacts.model.ContactsRandom
import com.example.contacts.ui.theme.ContactsTheme
import com.example.contacts.ui.theme.mediumTextStyleSize18
import com.example.contacts.ui.theme.mediumTextStyleSize20
import com.example.contacts.ui.theme.mediumTextStyleSize28
import com.example.contacts.ui.theme.primaryTextColor
import com.example.contacts.ui.theme.white
import com.example.contacts.viewmodel.ContactsState
import com.example.contacts.viewmodel.ContactsViewModel

sealed class Contact {
    data class Random(val contact: ContactsRandom) : Contact()
    data class Phone(val contact: ContactsPhone) : Contact()
}

@Composable
fun ContactScreen(navController: NavController, viewModel: ContactsViewModel) {
    var state by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit ){
        viewModel.fetchContactsRandomNetwork()
        viewModel.fetchContactsRandom()
    }

//    val scrollState = rememberLazyListState()
//
//    LaunchedEffect(scrollState) {
//        snapshotFlow { scrollState.layoutInfo.visibleItemsInfo }
//            .collect { visibleItems ->
//                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0
//                val totalItemCount = scrollState.layoutInfo.totalItemsCount
//
//                if (lastVisibleItemIndex == totalItemCount - 1 && state == 0) {
//                    viewModel.fetchContactsRandomNetwork()
//                    viewModel.fetchContactsRandom()
//                    Log.d("lastVisibleItemIndex","Trigger")
//                }
//            }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ContactsHeader()
        SearchBar(hint = "Search", onTextChange = { query ->
            searchQuery = query
        })
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        TabScreen(onTabSelected = { selectedTabIndex ->
            state = selectedTabIndex
            if (selectedTabIndex == 1) {
                viewModel.fetchContactsPhone()
            }
        })

        val filteredList = when (state) {
            0 -> viewModel.contactListRandom.value.filter { contact ->
                contact.firstName?.contains(searchQuery, ignoreCase = true) == true ||
                        contact.lastName?.contains(searchQuery, ignoreCase = true) == true
            }.map { Contact.Random(it) }
            else -> viewModel.contactListPhone.value.filter { contact ->
                contact.name.contains(searchQuery, ignoreCase = true)
            }.map { Contact.Phone(it) }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {

                items(filteredList) { contact ->
                    when (contact) {
                        is Contact.Random -> {
                            val randomContact = contact.contact
                            ContactItem(
                                onItemClick = {
                                    navController.navigate("${Screen.CreateContactScreen.route}/${randomContact.id}?source=random")
                                },
                                imageUrl = randomContact.pictureMedium ?: "",
                                fullName = randomContact.firstName ?: "",
                                lastName = randomContact.lastName ?: ""
                            )
                        }
                        is Contact.Phone -> {
                            val phoneContact = contact.contact
                            ContactItem(
                                onItemClick = {
                                    navController.navigate("${Screen.CreateContactScreen.route}/${phoneContact.id}?source=phone")
                                },
                                fullName = phoneContact.name,
                                lastName = "",
                                imageUrl = null
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        CommonText(
                            modifier = Modifier.clickable {
                                viewModel.fetchContactsRandomNetwork()
                                viewModel.fetchContactsRandom()
                            },
                            text = "Load More",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.mediumTextStyleSize18
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.CenterVertically)
                    ) {
                        if (viewModel.randomContactsState.value == ContactsState.Loading ||
                            viewModel.phoneContactsState.value == ContactsState.Loading
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                color = Color.Black

                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.CenterVertically)
                    ) {
                        if (viewModel.randomContactsState.value == ContactsState.Error("No Contacts") ||
                            viewModel.phoneContactsState.value == ContactsState.Error("No Contacts")
                        ) {

                            CommonText(
                                modifier = Modifier.fillMaxWidth(),
                                text = "No Contacts",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.mediumTextStyleSize28
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    navController.navigate("${Screen.CreateContactScreen.route}/$state")

                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Add,
                    modifier = Modifier.size(34.dp),
                    contentDescription = "Add"
                )
            }
        }
    }
}


@Composable
fun ContactItem(
    onItemClick: () -> Unit,
    imageUrl: String?,
    fullName: String,
    lastName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clickable(onClick = { onItemClick.invoke() }),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .size(32.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.blank_profile_picture),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            CommonText(
                modifier = Modifier,
                text = fullName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.mediumTextStyleSize18
            )

            Spacer(modifier = Modifier.width(8.dp))

            CommonText(
                modifier = Modifier,
                text = lastName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.mediumTextStyleSize18
            )
        }
    }
}


@Composable
fun ContactsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.Top,
        ) {
        CommonText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.app_name),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.mediumTextStyleSize28
        )
    }
}

@Composable
fun SearchBar(
    hint: String,
    modifier: Modifier = Modifier,
    isEnabled: (Boolean) = true,
    height: Dp = 40.dp,
    elevation: Dp = 3.dp,
    cornerShape: Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = white,
    onSearchClicked: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
) {
    var text by remember { mutableStateOf(TextFieldValue()) }
    Row(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = cornerShape)
            .background(color = backgroundColor, shape = cornerShape)
            .clickable { onSearchClicked() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = modifier
                .weight(5f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = text,
            onValueChange = {
                text = it
                onTextChange(it.text)
            },
            enabled = isEnabled,
            textStyle = MaterialTheme.typography.mediumTextStyleSize18,
            decorationBox = { innerTextField ->
                if (text.text.isEmpty()) {
                    CommonText(
                        text = hint,
                        textColor = primaryTextColor,
                        style = MaterialTheme.typography.mediumTextStyleSize20,
                    )
                }
                innerTextField()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearchClicked() }),
            singleLine = true
        )
        Box(
            modifier = modifier
                .weight(1f)
                .size(40.dp)
                .background(white, shape = CircleShape)
                .clickable {
                    if (text.text.isNotEmpty()) {
                        text = TextFieldValue(text = "")
                        onTextChange("")
                    }
                },
        ) {
            if (text.text.isNotEmpty()) {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .size(34.dp),
                    painter = painterResource(id = R.drawable.baseline_clear_24),
                    contentDescription = null,
                    tint = primaryTextColor,
                )
            } else {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = null,
                    tint = primaryTextColor,
                )
            }
        }
    }
}


@Composable
fun TabScreen(onTabSelected: (Int) -> Unit) {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Random", "Phone")
    Column {
        TabRow(
            selectedTabIndex = state,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = Color.Black,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state])
                )
            },
            containerColor = Color.White,
            modifier = Modifier
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    text = {
                        CommonText(
                            modifier = Modifier.fillMaxWidth(),
                            text = title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.mediumTextStyleSize20
                        )
                    },
                    selected = state == index,
                    onClick = {
                        state = index
                        onTabSelected(index)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactScreenPreview() {
    ContactsTheme {
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        Column {
            ContactsHeader()
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            SearchBar(hint = "Search", onTextChange = { })
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            TabScreen(onTabSelected = { index ->
                selectedTabIndex = index
            })
            ContactItem({},"https://randomuser.me/api/portraits/thumb/men/33.jpg","Gopinath","Subramani")
        }
    }
}


