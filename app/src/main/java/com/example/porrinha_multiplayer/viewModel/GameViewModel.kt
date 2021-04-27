package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference

object GameViewModel {
    lateinit var playerRef : DatabaseReference
    lateinit var roomRef : DatabaseReference
    lateinit var playersRef : DatabaseReference

    fun setPlayerReference(roomName: String, playerName: String) {
        playerRef = FirebaseRepository.getReference("rooms/$roomName/players/$playerName")
        roomRef = FirebaseRepository.getReference("rooms/$roomName")
        playersRef = FirebaseRepository.getReference("rooms/$roomName/players")
    }

    fun setPlayerReferenceValue(player: Player) {
        FirebaseRepository.setValue(playerRef, player)
    }

    fun setPlayerIsOnline(b: Boolean) {
        FirebaseRepository.setValue(playerRef.child("online"), b)
    }

    fun getTotalSticks(players: Map<String, Player>?): Int {
        var total = 0
        if (players != null) {
            for (player in players.values){
                total += player.totalSticks!!
            }
        }
        return total
    }


}

