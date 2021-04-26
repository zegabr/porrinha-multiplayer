package com.example.porrinha_multiplayer

import android.R
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.porrinha_multiplayer.databinding.ActivityGameBinding
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.viewModel.GameViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class GameActivity : AppCompatActivity() {
    // TODO: criar logica do jogo aqui
    // TODO: remover user da room através de um botao de sair

    lateinit var playButton: Button
    lateinit var exitButton: Button
    lateinit var roomNameTextView: TextView
    lateinit var roundTextView: TextView
    lateinit var playersListView: ListView
    lateinit var totalSticksTextview: TextView
    lateinit var lastRoundSticksTextView: TextView
    lateinit var playerSticksTextView: TextView
    lateinit var sticksToPlay: EditText

    var playerName = ""
    var roomName = ""
    var role = false
    var sticks = 0
    lateinit var playersList: MutableList<String>

    lateinit var playerObject: Player
    lateinit var room: Room
    lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomNameTextView = binding.roomNameTextView
        roundTextView = binding.roundTextView
        playersListView = binding.playersListView
        totalSticksTextview = binding.totalSticksTextview
        lastRoundSticksTextView = binding.lastRoundSticksTextView
        playerSticksTextView = binding.playerSticksTextView
        sticksToPlay = binding.sticksToPlay

        playButton = binding.playButton
        playButton.isEnabled = true

        playersList = mutableListOf()
        playerName = getPlayerNameFromCache() // TODO: esse metodo aparece em todas as activities implementadas até agr

        val extras = intent.extras
        if (extras != null) {
            roomName = extras.getString("roomName").toString()
            roomNameTextView.text = roomName
        }
        GameViewModel.setPlayerReference(roomName, playerName)
        addPlayerEventListener() // escuta mudancas no player
        addRoomEventListener() // escuta mudanças na sala
        addPlayersListEventListener()
        GameViewModel.setPlayerIsOnline(true) // deve triggar os 2 eventlistener

        playButton.setOnClickListener {
            if (sticksToPlay.text.isBlank()) {
                Toast.makeText(applicationContext, "Must chose an integer", Toast.LENGTH_SHORT).show()
            } else {
                sticks = sticksToPlay.text.toString().toInt()
                if (sticks > playerObject.totalSticks!!) {
                    Toast.makeText(applicationContext, "This is more than you have", Toast.LENGTH_SHORT).show()
                } else if (sticks == 0) {
                    Toast.makeText(applicationContext, "Quantity not allowed", Toast.LENGTH_SHORT).show()
                } else {
                    playerObject.played = true
                    playerObject.selectedSticks = sticks
                    playButton.isEnabled = false
                    GameViewModel.setPlayerReferenceValue(playerObject)
                }
            }
        }
    }

    private fun addPlayersListEventListener() {
        GameViewModel.playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // show list of rooms
                playersList.clear()
                val players = snapshot.children

                for (player in players) {
                    playersList.add(player.key.toString())
                }
                playersListView.adapter = ArrayAdapter(this@GameActivity, R.layout.simple_list_item_1, playersList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameActivity, "Error reading list of players", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addRoomEventListener() {
        GameViewModel.roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                room = snapshot.getValue(Room::class.java)!!
                updateRoomValuesOnScreen(room)

                // TODO: if host and everyone has played, then try processing game

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error reading room values", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addPlayerEventListener() { // observa mudanca no player
        GameViewModel.playerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                playerObject = snapshot.getValue(Player::class.java)!! // assim q pega classe, nao da pra pegar fora desse listener, nao consegui acessar o datasnapshot
                playerSticksTextView.text = playerObject.totalSticks.toString()
                playButton.isEnabled = playerObject.played == false // só pode play se nao tiver played

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun updateRoomValuesOnScreen(room: Room) {
        roundTextView.text = room.currentRound.toString() + "/" + room.maxRounds.toString()
        totalSticksTextview.text = GameViewModel.getTotalSticks(room.players).toString()
        lastRoundSticksTextView.text = room.lastRoundSticks.toString()


    }

    private fun getPlayerNameFromCache(): String {
        val preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("playerName", "").toString()
    }
}