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

class GameActivity : AppCompatActivity() {
    // TODO: criar logica do jogo aqui
    // TODO: remover user da room através de um botao de sair
    // TODO: fazer o app começar daqui qnd o user tiver numa room (assim como começa no lobby qnd tem user no cache)

    lateinit var button: Button
    var playerName = ""
    var roomName = ""
    var role = ""
    var message = ""
    lateinit var database : FirebaseDatabase
    lateinit var messageRef : DatabaseReference
    lateinit var binding: ActivityGameBinding


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
        messageRef = database.getReference("rooms").child(roomName).child("message")
        addRoomEventListener() // listen for incoming messages

        button.setOnClickListener {
            // send message
            message = playerName
            messageRef.setValue(message)
        }

        message = playerName
        messageRef.setValue(message)

    }

    private fun getPlayerNameFromCache(): String {
        val preferences : SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("playerName", "").toString()
    }

    private fun addRoomEventListener() { // observa mudança na mensagem
        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // message recieved
                Toast.makeText(applicationContext,  snapshot.value.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // error -> retry
                messageRef.setValue(message)
            }
        })
    }
}