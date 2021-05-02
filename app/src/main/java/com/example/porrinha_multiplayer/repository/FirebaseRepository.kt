package com.example.porrinha_multiplayer.repository

import android.location.Location
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// https://blog.mindorks.com/how-to-create-a-singleton-class-in-kotlin#:~:text=A%20singleton%20class%20is%20a,like%20NetworkService%2C%20DatabaseService%2C%20etc.
object FirebaseRepository {
    // Singleton class
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getReference(url: String): DatabaseReference {
        return database.getReference(url)
    }

    fun setValue(databaseReference: DatabaseReference, value: String) {
        databaseReference.setValue(value)
    }

    fun setValue(databaseReference: DatabaseReference, value: Location) {
        databaseReference.setValue(value)
    }

    fun setValue(databaseReference: DatabaseReference, value: Double) {
        databaseReference.setValue(value)
    }

    fun setValue(databaseReference: DatabaseReference, value: Player) {
        databaseReference.setValue(value)
    }

    fun setValue(databaseReference: DatabaseReference, b: Boolean) {
        databaseReference.setValue(b)
    }

    fun setValue(databaseReference: DatabaseReference, i: Int) {
        databaseReference.setValue(i)
    }

    fun setValue(databaseReference: DatabaseReference, value: User) {
        databaseReference.setValue(value)
    }

    fun setValue(databaseReference: DatabaseReference, room: Room) {
        databaseReference.setValue(room)
    }

    fun removeReference(databaseReference: DatabaseReference) {
        databaseReference.removeValue()
    }

}