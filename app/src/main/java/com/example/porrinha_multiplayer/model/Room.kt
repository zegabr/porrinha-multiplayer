package com.example.porrinha_multiplayer.model

data class Room(
        var currentRound: Int? = 0,
        var lastRoundSticks: Int? = -1,
        var maxRounds: Int? = 0,
        var players: Map<String,Player>? = null
)