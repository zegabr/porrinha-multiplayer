package com.example.porrinha_multiplayer

import com.google.firebase.database.IgnoreExtraProperties

data class Player(val name: String? = null, var selectedSticks: Int? = 0, var totalSticks: Int? = 0, var played: Boolean? = false, val host: Boolean? = false)
// ESSE é pra ser usado em jogo, nao como user
