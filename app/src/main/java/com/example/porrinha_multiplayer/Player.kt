package com.example.porrinha_multiplayer

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Player(val name: String, var selectedSticks: Int, var totalSticks: Int, var played: Boolean)
// ESSE é pra ser usado em jogo, nao como user
