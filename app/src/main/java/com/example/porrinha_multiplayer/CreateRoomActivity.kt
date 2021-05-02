package com.example.porrinha_multiplayer

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.porrinha_multiplayer.databinding.ActivityCreateRoomBinding
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.viewModel.GameViewModel
import com.example.porrinha_multiplayer.viewModel.LobbyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CreateRoomActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateRoomBinding
    lateinit var preferences: SharedPreferences
    lateinit var createRoomButton: Button
    lateinit var goBackButton: Button
    lateinit var roomNameInput: EditText
    lateinit var playersLimitInput: EditText
    lateinit var addRoomListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Setando os bindings
        roomNameInput = binding.roomNameInput
        playersLimitInput = binding.userLimitInput
        goBackButton = binding.backButton
        createRoomButton = binding.createRoomButton
        addCreateRoomButtonEventListener() //Criando o listener de criação de sala
        addBackButtonEventListener() //Criando o listener de voltar a lista de lobbys
        preferences = getSharedPreferences("PREFS", 0)
    }

    private fun finishActivity() {
        //Removendo o event listener
        LobbyViewModel.roomsRef.removeEventListener(addRoomListener)
        finish()
    }

    private fun addCreateRoomButtonEventListener() {
        createRoomButton.setOnClickListener {
            // cria sala e adiciona o user como um player novo
            var username = intent.getStringExtra("name")
            var userLatitude = intent.getDoubleExtra("latitude", 0.0)
            var userLongitude = intent.getDoubleExtra("longitude", 0.0)
            var roomName = roomNameInput.text.toString()
            if (playersLimitInput.text.isBlank()){
                Toast.makeText(this@CreateRoomActivity, "O número de jogadores não pode ser vazio", Toast.LENGTH_SHORT).show()
            }else{
                var maxPlayers = playersLimitInput.text.toString().toInt()

                if(maxPlayers < 2){
                    Toast.makeText(this@CreateRoomActivity, "O mínimo de jogadores é 2", Toast.LENGTH_SHORT).show()
                }else if (roomName.equals("")){
                    Toast.makeText(this@CreateRoomActivity, "O nome da sala não pode ser vazio", Toast.LENGTH_SHORT).show()
                } else {
                    createRoomButton.setText("CREATING ROOM")
                    createRoomButton.isEnabled = false
                    LobbyViewModel.setRoomReference(roomName)
                    LobbyViewModel.initRoom(userLatitude, userLongitude, roomName, maxPlayers)
                    GameViewModel.setPlayerReference(roomName, username!!)
                    addRoomEventListener()
                    GameViewModel.setPlayerReferenceValue(Player(username, 0, -1, 3,false, true, true)) // adiciona o player na sala como host
                }
            }

        }
    }

    private fun addBackButtonEventListener() {
        goBackButton.setOnClickListener{
            val intent = Intent(this@CreateRoomActivity, LobbyActivity::class.java)
            startActivity(intent)
            finishActivity() // encerra activity atual
        }
    }

    private fun addRoomEventListener() {
        addRoomListener = GameViewModel.playerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return
                // join room
                enableCreateButton()
                goToGameScreen()
            }

            override fun onCancelled(error: DatabaseError) {
                // error
                enableCreateButton()
                Toast.makeText(this@CreateRoomActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun enableCreateButton() {
        createRoomButton.setText("CREATE ROOM")
        createRoomButton.isEnabled = true
    }

    private fun addCurrentRoomToCache() {
        val editor = preferences.edit()
        editor.putString("roomName", roomNameInput.text.toString()) // seta o valor user
        editor.apply() // adicionou o nome da sala na cache, qnd logar de novo ele vai estar lá
    }

    private fun goToGameScreen() {
        addCurrentRoomToCache()
        val intent = Intent(this@CreateRoomActivity, GameActivity::class.java)
        startActivity(intent)
        finishActivity() // encerra activity atual
    }
}