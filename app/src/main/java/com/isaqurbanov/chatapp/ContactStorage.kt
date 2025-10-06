package com.isaqurbanov.chatapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContactStorage(private val context: Context) {
    private val gson = Gson()
    private val fileName = "contacts.json"

    fun saveContacts(contacts: List<Contact>) {
        val json = gson.toJson(contacts)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun loadContacts(): MutableList<Contact> {
        return try {
            val json = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val type = object : TypeToken<MutableList<Contact>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            mutableListOf() // return empty if no file
        }
    }

    fun deleteContact(name: String) {
        val contacts = loadContacts().toMutableList()
        val updated = contacts.filterNot { it.name == name }
        saveContacts(updated)
    }


}
