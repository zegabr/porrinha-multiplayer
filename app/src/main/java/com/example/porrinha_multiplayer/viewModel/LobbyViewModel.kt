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

    fun initRoom(latitude: Double, longitude: Double, name: String) {
        FirebaseRepository.setValue(roomRef.child("latitude"), latitude)
        FirebaseRepository.setValue(roomRef.child("longitude"), longitude)
        FirebaseRepository.setValue(roomRef.child("name"), name)
        FirebaseRepository.setValue(roomRef.child("maxRounds"), 2)
        FirebaseRepository.setValue(roomRef.child("lastRoundSticks"), -1)
        FirebaseRepository.setValue(roomRef.child("currentRound"), 1)
    }

    lateinit var roomsRef : DatabaseReference
    lateinit var roomRef : DatabaseReference
}