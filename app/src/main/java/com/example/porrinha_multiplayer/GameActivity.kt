package com.example.porrinha_multiplayer

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.example.porrinha_multiplayer.databinding.ActivityGameBinding
import com.google.firebase.database.*
import kotlin.random.Random

class GameActivity : AppCompatActivity() {
    // TODO: criar logica do jogo aqui
    // TODO: remover user da room através de um botao de sair
    // TODO: fazer o app começar daqui qnd o user tiver numa room (assim como começa no lobby qnd tem user no cache)

    lateinit var button: Button
    var playerName = ""
    var roomName = ""
    var role = ""
    var sticks = 0
    lateinit var database : FirebaseDatabase
    lateinit var playerRef : DatabaseReference
    lateinit var binding: ActivityGameBinding
    lateinit var player : Player


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = binding.buttonGame
        button.isEnabled = true
        database = FirebaseDatabase.getInstance()

        playerName = getPlayerNameFromCache()

        val extras = intent.extras
        if(extras != null){
            roomName = extras.getString("roomName").toString()
            if(roomName.equals(playerName)){
                role = "host"
            }else{
                role = "guest"
            }
        }
        playerRef = database!!.getReference("rooms").child(roomName).child("players").child(playerName)
        addPlayerEventListener() // escuta mudancas no player

        button.setOnClickListener {
            // send message
            playerRef.child("totalSticks").setValue(ServerValue.increment(1))
            Toast.makeText(this@GameActivity,  sticks.toString(), Toast.LENGTH_SHORT).show() // TODO: ta printando 1,2,1,2,1,2,1?? ta criando uma activity em cima da outra aparentement. Resolver isso urgente
        }
    }

    private fun getPlayerNameFromCache(): String {
        val preferences : SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("playerName", "").toString()
    }

    private fun addPlayerEventListener() { // observa mudanca no player
        playerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // message recieved
                val player =  snapshot.getValue(Player::class.java) // assim q pega classe, nao da pra pegar fora desse listener, nao consegui acessar o datasnapshot
                if (player != null) {
                    sticks = player.totalSticks!!
//                    Toast.makeText(applicationContext,  player.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // error -> retry
                playerRef.child("totalSticks").setValue(ServerValue.increment(1))
            }
        })
    }
}