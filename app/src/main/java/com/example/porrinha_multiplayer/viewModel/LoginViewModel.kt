package com.example.porrinha_multiplayer.viewModel

import android.location.Location
import com.example.porrinha_multiplayer.model.User
import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object LoginViewModel {
    lateinit var userReference: DatabaseReference

    fun setupUserReference(username: String) {
        userReference = FirebaseRepository.getReference("users/$username")
    }

    fun setUserReferenceValue(value: User) {
        FirebaseRepository.setValue(userReference, value)
    }
}