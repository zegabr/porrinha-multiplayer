package com.example.porrinha_multiplayer

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.porrinha_multiplayer.databinding.ActivityGameBinding
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.viewModel.GameViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class GameActivity : AppCompatActivity() {
    // TODO: criar logica do jogo aqui
    // TODO: remover user da room através de um botao de sair
    // TODO: fazer o app começar daqui qnd o user tiver numa room (assim como começa no lobby qnd tem user no cache)

    lateinit var button: Button
    var playerName = ""
    var roomName = ""
    var role = ""
    var sticks = 0

    lateinit var binding: ActivityGameBinding
    lateinit var player: Player


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = binding.buttonGame
        button.isEnabled = true

        playerName = getPlayerNameFromCache() // TODO: esse metodo aparece em todas as activities implementadas até agr

        val extras = intent.extras
        if (extras != null) {
            roomName = extras.getString("roomName").toString()
        }
        GameViewModel.setPlayerReference(roomName, playerName)
        addPlayerEventListener() // escuta mudancas no player

        button.setOnClickListener {
            // send message
            GameViewModel.setPlayerChildValue("totalSticks", 1)
        }
    }

    private fun getPlayerNameFromCache(): String {
        val preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("playerName", "").toString()
    }

    private fun addPlayerEventListener() { // observa mudanca no player
        GameViewModel.playerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // message recieved
                val player = snapshot.getValue(Player::class.java) // assim q pega classe, nao da pra pegar fora desse listener, nao consegui acessar o datasnapshot
                if (player != null) {
                    sticks = player.totalSticks!!
                    Toast.makeText(this@GameActivity, player.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // error -> retry
                GameViewModel.setPlayerChildValue("totalSticks", 1)
            }
        })
    }
}