package com.example.porrinha_multiplayer.model

data class Player(
        val name: String? = null,
        var selectedSticks: Int? = 0,
        var finalGuess: Int? = 0,
        var totalSticks: Int? = 0,
        var played: Boolean? = false,
        var host: Boolean? = false,
        val online: Boolean? = false // TODO: checar se realmente precisa disso
)
