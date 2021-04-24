package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object GameViewModel {
    lateinit var playerRef : DatabaseReference
    fun setPlayerReference(roomName: String, playerName: String) {
        playerRef = FirebaseRepository.getReference("rooms/$roomName/players/$playerName")
    }

    fun setPlayerReferenceValue(player: Player) {
        FirebaseRepository.setValue(playerRef, player)
    }

    fun setPlayerChildValue(url: String, i: Long) {
        FirebaseRepository.incremenChildInteger(playerRef, url, i)
    }
}

