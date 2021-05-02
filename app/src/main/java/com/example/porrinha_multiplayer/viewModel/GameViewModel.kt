package com.example.porrinha_multiplayer.viewModel

import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.repository.FirebaseRepository
import com.google.firebase.database.DatabaseReference
import kotlin.math.abs
import kotlin.math.max

object GameViewModel {
    var inGame: Boolean = false
    var wonLastRound: Int = 0
    var isHost: Boolean = false
    lateinit var playerRef: DatabaseReference
    lateinit var roomRef: DatabaseReference
    lateinit var playersRef: DatabaseReference

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

    fun setPlayerName(name: String) {
        FirebaseRepository.setValue(playerRef.child("name"), name)
    }

    fun getTotalSticks(players: Map<String, Player>?): Int {
        var total = 0
        if (players != null) {
            for (player in players.values) {
                total += player.totalSticks!!
            }
        }
        return total
    }

    fun processGameState(room: Room): Room {
        var players = room.players
        val totalSelectedSticks =
            this.getTotalSelectedSticks(players) // soma de sticks selecionados na rodada

        players = updateWinnersAndLoosers(players, totalSelectedSticks)

        room.players = players
        room.lastRoundSticks = totalSelectedSticks
        room.currentRound = room.currentRound?.plus(1)
        room.processing = false
        return room
    }

    private fun updateWinnersAndLoosers(
        players: Map<String, Player>?,
        totalSelectedSticks: Int
    ): Map<String, Player>? {
        var maxDifference = 0
        if (players != null) {
            for (player in players) { // pega a diferenca maxima
                maxDifference =
                    max(maxDifference, abs(totalSelectedSticks - player.value.finalGuess!!))
                player.value.played = false
            }
            for (player in players) { // remove um de todos que tem diferenca igual a diferenca maxima
                val playerDifference = abs(totalSelectedSticks - player.value.finalGuess!!)
                if (playerDifference == maxDifference) {
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
            for (player in players.values) {
                total += player.selectedSticks!!
            }
        }
        return total
    }

    fun allPlayersHavePlayed(players: Map<String, Player>?): Boolean {
        if (players != null) {
            for (player in players.values) {
                if (player.played == false) {
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
            for (player in players.values) {
                if (player.host == true) {
                    return true
                }
            }
        }
        return false
    }

    fun setFirstPlayerAsHost(
        players: Map<String, Player>?,
        playerName: String
    ): Map<String, Player>? {
        if (players != null) {
            for (player in players.values) {
                if (player.host == false) {
                    player.host = true // faz o 1o do map ser host e para
                    if (playerName.equals(player.name)) {
                        this.isHost = true
                    }
                    return players
                }
            }
        }
        return players
    }

    fun playerWon(room: Room, player: Player): Boolean {
        val players = room.players
        if (players != null) {
            return (players.size == 1 && room.currentRound != 1)
        } // só existe 1 player e já passou da 1a rodada
        return false
    }

    fun updateMaxRounds(newMaxRounds: Int) {
        FirebaseRepository.setValue(roomRef.child("maxRounds"), newMaxRounds)
    }

    fun playerLost(player: Player): Boolean {
        return player.totalSticks == 0
    }

    fun setProcessing() {
        FirebaseRepository.setValue(roomRef.child("processing"), true)
    }


}

