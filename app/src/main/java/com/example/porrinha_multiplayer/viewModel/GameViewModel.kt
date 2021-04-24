package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object GameViewModel {
    lateinit var playerRef : DatabaseReference
    fun setPlayerReference(url: String) {
        playerRef = FirebaseRepository.getReference(url)
    }

    fun setPlayerReferenceValue(player: Player) {
        FirebaseRepository.setValue(playerRef, player)
    }
}

