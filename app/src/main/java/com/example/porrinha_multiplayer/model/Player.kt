package com.example.porrinha_multiplayer.model

data class Player(
        val name: String? = null,
        var selectedSticks: Int? = 0,
        var finalGuess: Int? = 0,
        var totalSticks: Int? = 0,
        var played: Boolean? = false,
        var host: Boolean? = false,
        val online: Boolean? = false // TODO: talvez a gt n precise disso
)
// ESSE é pra ser usado em jogo, nao como user
