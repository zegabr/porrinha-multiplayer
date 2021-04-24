package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object LobbyViewModel {
    fun setRoomReference(roomName: String) {
        roomRef = FirebaseRepository.getReference("rooms/$roomName")
    }

    fun setRoomsReference() {
        roomsRef = FirebaseRepository.getReference("rooms")
    }

    lateinit var roomsRef : DatabaseReference
    lateinit var roomRef : DatabaseReference


}