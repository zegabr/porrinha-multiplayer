package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object LoginViewModel {
    lateinit var userReference: DatabaseReference

    fun setupUserReference(username: String) {
        userReference = FirebaseRepository.getReference("users/$username")
    }

    fun setUserReferenceValue(value: String) { // TODO: trocar pra User
        FirebaseRepository.setValue(userReference, value)
    }


}