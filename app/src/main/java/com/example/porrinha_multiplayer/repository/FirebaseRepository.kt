package com.example.porrinha_multiplayer.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// https://blog.mindorks.com/how-to-create-a-singleton-class-in-kotlin#:~:text=A%20singleton%20class%20is%20a,like%20NetworkService%2C%20DatabaseService%2C%20etc.
object FirebaseRepository {
    // Singleton class
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getReference(url: String): DatabaseReference {
        return database.getReference(url)
    }

    fun setValue(databaseReference: DatabaseReference, value:String) {
        databaseReference.setValue(value)
    }

}