package com.isaqurbanov.chatapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color


data class Contact(val name: String)




@Composable
fun ContactsScreen(sender: String, navController: NavController) {
    val context = LocalContext.current
    val storage = remember { ContactStorage(context) }
    var contacts by remember { mutableStateOf(listOf<Contact>()) }

    // Load contacts initially
    LaunchedEffect(Unit) {
        contacts = storage.loadContacts()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("contact-add-screen") },
                containerColor = myDarkGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "My Contacts",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(contacts) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clickable {
                                // Navigate to chat
                                navController.navigate("chat-screen/$sender/${contact.name}")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Icon
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Contact Name
                        Text(
                            text = contact.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        // Delete button
                        IconButton(onClick = {
                            storage.deleteContact(contact.name)
                            contacts = contacts.filter { it.name != contact.name } // refresh without reloading
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }

                    Divider()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewContactScreen() {
    val navController = rememberNavController()
    val sender = "Isa"
    ContactsScreen(sender, navController)
}
