package com.isaqurbanov.chatapp


import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

@Serializable
data class ChatMessageDto(
    val sender: String,
    val recipient: String,
    val content: String
)

class ChatWebSocket(
    private val scope: CoroutineScope
) {
    private val wsClient = OkHttpWebSocketClient()
    private val stompClient = StompClient(wsClient)

    private var session: StompSession? = null

    private val _parsedMessages = MutableSharedFlow<ChatMessageDto>(extraBufferCapacity = 64)
    val parsedMessages: SharedFlow<ChatMessageDto> = _parsedMessages.asSharedFlow()

    suspend fun connect(username: String) {
        session = stompClient.connect("ws://public-chat-web.onrender.com/ws/websocket")

        // Subscribe to /topic/messages
        val subscription = session!!.subscribeText("/queue/private.$username")

        scope.launch(Dispatchers.IO) {
            subscription.collect { msgText ->
                try {
                    val chatMessage = Json.decodeFromString<ChatMessageDto>(msgText)
                    _parsedMessages.emit(chatMessage)
                } catch (e: SerializationException) {
                    Log.e("ChatWebSocket", "Error parsing message: $msgText", e)
                }
            }
        }
    }

    suspend fun sendMessage(sender: String, recipient: String, content: String) {
        val chatMessage = ChatMessageDto(sender, recipient, content)
        val jsonPayload = Json.encodeToString(chatMessage)
        session?.sendText("/app/chat.sendPrivate", jsonPayload)
    }

    suspend fun disconnect() {
        session?.disconnect()
        session = null
    }
}