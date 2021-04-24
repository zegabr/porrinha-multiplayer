package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object LobbyViewModel {
    fun setRoomReference(url: String) {
        roomRef = FirebaseRepository.getReference(url)
    }

    fun setRoomsReference(url: String) {
        roomsRef = FirebaseRepository.getReference(url)
    }

    lateinit var roomsRef : DatabaseReference
    lateinit var roomRef : DatabaseReference


}