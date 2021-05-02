package com.example.porrinha_multiplayer.viewModel

import android.content.Context
import android.widget.Toast
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference
import kotlin.math.abs
import kotlin.math.max

object GameViewModel {
    var wonLastRound: Int = 0
    var isHost: Boolean = false
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

    fun updateRoom(room: Room) {
        FirebaseRepository.setValue(roomRef, room)
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

    fun processGameState(room: Room, playerName: String, applicationContext: Context): Room {
        var players = room.players
        var totalSelectedSticks = this.getTotalSelectedSticks(players) // soma de sticks selecionados na rodada

        players = updateWinnersAndLoosers(players, totalSelectedSticks)

        room.players = players
        room.lastRoundSticks = totalSelectedSticks
        room.currentRound = room.currentRound?.plus(1)
        return room
    }

    private fun updateWinnersAndLoosers(
        players: Map<String, Player>?,
        totalSelectedSticks: Int
    ): Map<String, Player>? {
        var maxDifference = 0
        if (players != null) {
            for (player in players){ // pega a diferenca maxima
                maxDifference = max(maxDifference, abs(totalSelectedSticks - player.value.selectedSticks!!))
                player.value.played = false
            }
            for (player in players){ // remove um de todos que tem diferenca igual a diferenca maxima
                if(abs(totalSelectedSticks - player.value.selectedSticks!!) == maxDifference){
                    player.value.totalSticks = player.value.totalSticks?.minus(1)
                    this.wonLastRound = 2
                }else{
                    this.wonLastRound = 1
                }
            }
        }
        return players
    }

    private fun getTotalSelectedSticks(players: Map<String, Player>?): Int {
        var total = 0
        if (players != null) {
            for (player in players.values){
                total += player.selectedSticks!!
            }
        }
        return total
    }

    fun allPlayersHavePlayed(players: Map<String, Player>?): Boolean {
        if (players != null) {
            for (player in players.values){
                if (player.played == false){
                    return false
                }
            }
        }
        return true
    }

    fun removePlayerFromRoom() {
        FirebaseRepository.removeReference(playerRef)
    }

    fun finnishRoom() {
        FirebaseRepository.removeReference(roomRef)
    }

    fun hasHost(players: Map<String, Player>?): Boolean {
        if (players != null) {
            for (player in players.values){
                if (player.host == true){
                    return true
                }
            }
        }
        return false
    }

    fun setFirstPlayerAsHost(players: Map<String, Player>?, playerName: String): Map<String, Player>? {
        if (players != null) {
            for (player in players.values){
                if (player.host == false){
                    player.host = true // faz o 1o do map ser host e para
                    if(playerName.equals(player.name)){
                        this.isHost = true
                    }
                    return players
                }
            }
        }
        return players
    }

    fun playerWon(room: Room, players: Map<String, Player>): Boolean {
        return room.currentRound!! > room.maxRounds!! // acabou as rodadas
                || (players.size == 1 && room.currentRound != 1) // só existe 1 player e já passou da 1a rodada
    }


}

