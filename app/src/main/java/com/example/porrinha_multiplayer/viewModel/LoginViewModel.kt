package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object LoginViewModel {
    lateinit var userReference: DatabaseReference

    fun setupUserReference(url: String) {
        userReference = FirebaseRepository.getReference(url)
    }

    fun setUserReferenceValue(value: String) { // TODO: trocar pra User
        FirebaseRepository.setValue(userReference, value)
    }


}