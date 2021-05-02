package com.example.porrinha_multiplayer.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.example.porrinha_multiplayer.databinding.PlayersGameBinding
import com.example.porrinha_multiplayer.model.Player

class PlayersViewHolder(private val binding: PlayersGameBinding) :
        RecyclerView.ViewHolder(binding.root)
{
    var playerName : String = ""
    var actualSticksCount : String = "0"

    fun bindTo(player : Player) {
        //Fazendo o binding do jogo no rooms linearlayout
        playerName = player.name!!
        actualSticksCount = player.totalSticks!!.toString()
        if(player.played!! == true){
            binding.isReady.text = "Sim"
        }else {
            binding.isReady.text = "Não"
        }
        binding.playerName.text = playerName
        binding.sticksCount.text = actualSticksCount
    }
}