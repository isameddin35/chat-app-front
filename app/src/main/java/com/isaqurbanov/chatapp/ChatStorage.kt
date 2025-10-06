package com.isaqurbanov.chatapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ChatStorage(private val context: Context) {
    private val gson = Gson()

    private fun getFile(recipient: String): File {
        return File(context.filesDir, "chat_${recipient}.json")
    }

    fun saveMessages(recipient: String, messages: List<ChatMessageDto>) {
        val json = gson.toJson(messages)
        getFile(recipient).writeText(json)
    }

    fun loadMessages(recipient: String): MutableList<ChatMessageDto> {
        val file = getFile(recipient)
        if (!file.exists()) return mutableListOf()
        val json = file.readText()
        val type = object : TypeToken<List<ChatMessageDto>>() {}.type
        return gson.fromJson<List<ChatMessageDto>>(json, type)?.toMutableList() ?: mutableListOf()
    }

    fun clearMessages(recipient: String) {
        val file = File(context.filesDir, "chat_$recipient.json")
        if (file.exists()) {
            file.delete()
        }
    }

}
