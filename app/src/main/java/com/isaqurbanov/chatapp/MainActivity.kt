package com.isaqurbanov.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.ui.text.TextStyle
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

val myDarkGreen = Color(0xFF01a38d)
val myLightGreen = Color(0xFF72d2b1)


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val context = LocalContext.current
    val storage = remember { ChatStorage(context) }

    // get recipient argument if available
    val recipientArg = navBackStackEntry?.arguments?.getString("recipient")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            currentDestination?.route?.startsWith("chat-screen") == true && recipientArg != null ->
                                "Chat with $recipientArg"
                            else -> "ChatsApp"
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = myDarkGreen,
                    titleContentColor = Color.White
                ),
                actions = {
                    if (currentDestination?.route?.startsWith("chat-screen") == true && recipientArg != null) {
                        IconButton(onClick = {
                            // delete persisted file
                            storage.clearMessages(recipientArg)

                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("clear_chat", true)

                            // signal ChatScreen to clear its in-memory messages
                            navController.currentBackStackEntry?.savedStateHandle?.set("clear_chat", true)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear chat"
                            )
                        }}
                }
            )
        },

    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = "first-page"
        ) {
            composable("first-page") { FirstPage(navController) }
            composable("contact-screen/{sender}") { backStackEntry ->
                val sender = backStackEntry.arguments?.getString("sender") ?: ""
                ContactsScreen(sender, navController)
            }
            composable("chat-screen/{sender}/{recipient}") { backStackEntry ->
                val sender = backStackEntry.arguments?.getString("sender") ?: ""
                val recipient = backStackEntry.arguments?.getString("recipient") ?: ""
                ChatScreen(sender, recipient, navController)
            }
            composable("contact-add-screen") { ContactAddScreen() }
        }
    }
}




@Composable
fun FirstPage(navController: NavController) {
    var sender by remember { mutableStateOf("") }

    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(myDarkGreen, myLightGreen) // blue gradient
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(0.9f),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome 👋",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = myDarkGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = sender,
                    onValueChange = { sender = it },
                    label = { Text("Adınızı daxil edin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = myDarkGreen,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        navController.navigate("contact-screen/$sender")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = myDarkGreen)
                ) {
                    Text(
                        text = "Connect",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(sender: String, recipient: String, navController: NavController) {
    val context = LocalContext.current
    val storage = remember { ChatStorage(context) }

    val scope = rememberCoroutineScope()
    val chatWebSocket = remember { ChatWebSocket(scope) }
    val messages = remember { mutableStateListOf<ChatMessageDto>() }
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle, recipient) {
        savedStateHandle?.getStateFlow("clear_chat", false)?.collect { shouldClear ->
            if (shouldClear) {
                messages.clear()
                storage.saveMessages(recipient, messages)
                savedStateHandle.set("clear_chat", false)
            }
        }
    }

    LaunchedEffect(recipient) {
        messages.clear()
        messages.addAll(storage.loadMessages(recipient))

        chatWebSocket.connect(sender)
        chatWebSocket.parsedMessages.collect { msg ->
            if (msg.sender == recipient) {
                messages.add(msg)
                storage.saveMessages(recipient, messages)

                scope.launch {
                    listState.animateScrollToItem(messages.lastIndex)
                }
            }
        }
    }

    // Gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(myLightGreen, myDarkGreen)
                )
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Chat messages
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                items(messages) { message ->
                    ChatMessageBubble(message = message, currentUser = sender)
                    Spacer(Modifier.height(6.dp))
                }
            }

            // Input Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = myDarkGreen,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            val newMessage = ChatMessageDto(sender, recipient, input)
                            chatWebSocket.sendMessage(sender, recipient = recipient, content = input)
                            messages.add(newMessage)
                            storage.saveMessages(recipient, messages)
                            input = ""
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = myDarkGreen),
                    modifier = Modifier.size(50.dp),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // Floating scroll-to-bottom button
        SmallFloatingActionButton(
            onClick = {
                scope.launch {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.lastIndex)
                    }
                }
            },
            containerColor = myDarkGreen,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Scroll to bottom",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageDto, currentUser: String) {
    val isMe = message.sender == currentUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomEnd = if (isMe) 0.dp else 16.dp,
                        bottomStart = if (isMe) 16.dp else 0.dp
                    )
                )
                .background(Color.White)
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = Color.Black,
                fontSize = 16.sp
            )
        }
    }
}



@Preview
@Composable
fun PreviewChatScreen() {
    val sender:String = ""
    val recipient:String = ""
    val navController = rememberNavController()
    ChatScreen(sender, recipient, navController)
}