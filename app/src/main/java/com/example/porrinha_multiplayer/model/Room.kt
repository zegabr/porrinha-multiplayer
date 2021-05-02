package com.example.porrinha_multiplayer.model

data class Room(
        var currentRound: Int? = 0,
        var lastRoundSticks: Int? = -1,
        var maxRounds: Int? = 0,
        var players: Map<String,Player>? = emptyMap<String,Player>(),
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var name: String = "",
        var maxPlayers: Int? = 0,
        var processing: Boolean = false
)