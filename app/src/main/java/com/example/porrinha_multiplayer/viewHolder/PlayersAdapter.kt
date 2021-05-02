package com.example.porrinha_multiplayer.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.porrinha_multiplayer.databinding.PlayersGameBinding
import com.example.porrinha_multiplayer.model.Player

class PlayersAdapter(
    private val players: MutableList<Player>,
    private val inflater: LayoutInflater
) :
    RecyclerView.Adapter<PlayersViewHolder>() {
    override fun getItemCount(): Int = players.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        val binding = PlayersGameBinding.inflate(inflater, parent, false)
        return PlayersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        holder.bindTo(players[position])
    }
}