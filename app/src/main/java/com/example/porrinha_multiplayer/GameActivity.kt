package com.example.porrinha_multiplayer

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.porrinha_multiplayer.databinding.ActivityGameBinding
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.viewHolder.PlayersAdapter
import com.example.porrinha_multiplayer.viewModel.GameViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlin.math.max

class GameActivity : AppCompatActivity() {

    lateinit var playButton: Button
    lateinit var exitButton: Button
    lateinit var roomNameTextView: TextView
    lateinit var roundTextView: TextView
    lateinit var playersRecyclerView: RecyclerView
    lateinit var totalSticksInRoomTextview: TextView
    lateinit var lastRoundStickSumTextView: TextView
    lateinit var playerTotalSticksTextView: TextView
    lateinit var sticksToPlayEditText: EditText
    lateinit var finalGuessEditText: EditText

    lateinit var playerEventListener : ValueEventListener
    lateinit var roomEventListener : ValueEventListener
    lateinit var playersListEventListener : ValueEventListener

    var playerName = ""
    var roomName = ""
    lateinit var playersList: MutableList<Player>

    lateinit var playerObject: Player
    lateinit var room: Room
    lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomNameTextView = binding.roomNameTextView
        roundTextView = binding.roundTextView
        playersRecyclerView = binding.playersRecycler
        totalSticksInRoomTextview = binding.totalSticksTextview
        lastRoundStickSumTextView = binding.lastRoundSticksTextView
        playerTotalSticksTextView = binding.playerSticksTextView
        sticksToPlayEditText = binding.sticksToPlay
        finalGuessEditText = binding.finalGuessEditText

        exitButton = binding.exitRoomButton
        playButton = binding.playButton
        playButton.isEnabled = true

        playersList = mutableListOf()
        playerName = getPlayerNameFromCache()

        roomName = getCurrentRoomFromCache()
        if (!roomName.equals("")) {
            roomNameTextView.text = roomName
        }
        GameViewModel.setPlayerReference(roomName, playerName)
        startPlayerEventListener() // escuta mudancas no player
        startRoomEventListener() // escuta mudan??as na sala
        startPlayersListEventListener()
        GameViewModel.setPlayerName(playerName) // deve triggar os 3 eventlistener
        GameViewModel.inGame = true
        playButton.setOnClickListener {
            if (sticksToPlayEditText.text.isBlank() || finalGuessEditText.text.isBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Must chose an integer for each field",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var sticksToPlay = sticksToPlayEditText.text.toString().toInt()
                var finalGuess = finalGuessEditText.text.toString().toInt()

                if (sticksToPlay > playerObject.totalSticks!!) {
                    Toast.makeText(
                        applicationContext,
                        "This is more than you have",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (sticksToPlay < 0 || finalGuess < 0) {
                    Toast.makeText(applicationContext, "Quantity not allowed", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    playerObject.played = true
                    playerObject.selectedSticks = sticksToPlay
                    playerObject.finalGuess = finalGuess
                    disableUI()

                    GameViewModel.setPlayerReferenceValue(playerObject)
                }
            }
        }

        exitButton.setOnClickListener {
            GameViewModel.removePlayerFromRoom()
            goToLobbyScreen()
        }
    }

    private fun startPlayersListEventListener() {
        playersListEventListener = GameViewModel.playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.hasChild(playerName)) return // evita fazer outras coisas se o player ja saiu da sala
                // show list of rooms
                playersList.clear()
                val players = snapshot.children

                for (player in players.iterator()) {
                    var actualPlayer = player.getValue(Player::class.java)
                    if (actualPlayer != null) {
                        playersList.add(actualPlayer)
                    }
                }
                playersRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@GameActivity)
                    addItemDecoration(
                        DividerItemDecoration(
                            this@GameActivity,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    adapter = PlayersAdapter(playersList, layoutInflater)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@GameActivity,
                    "Error reading list of players",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun startRoomEventListener() {
       roomEventListener = GameViewModel.roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || !snapshot.hasChild("players/$playerName")) return

                room = snapshot.getValue(Room::class.java)!!
                var players = room.players
                if (players != null) {

                    if (room.processing) { // se for fazer algo enquanto processa, s?? pode leitura
                        playerObject = players[playerName]!!
                        return
                    }


                    if (GameViewModel.isHost) {
                        // update maxRounds
                        GameViewModel.updateMaxRounds(
                            max(
                                room.maxRounds!!,
                                3 * players.values.size
                            )
                        )

                        if (players.size != 1 && GameViewModel.allPlayersHavePlayed(players)) {
                            // update processing pra true
                            GameViewModel.setProcessing()
                            // processa jogo seando processing pra false
                            room = GameViewModel.processGameState(room, playerName, this@GameActivity)
                            // update room com is processing pra false tbm
                            GameViewModel.updateRoom(room)
                        }
                    } else {
                        if (!GameViewModel.hasHost(players)) {
                            players = GameViewModel.setFirstPlayerAsHost(players, playerName)
                            room.players = players
                            GameViewModel.updateRoom(room)
                        }
                    }

                    if (players != null) {

                        playerObject = players[playerName]!!
                        if (playerObject.played == false) {
                            enableUI()
                        }

                        if (GameViewModel.playerWon(room, playerObject)) {
                            GameViewModel.finnishRoom()
                            goToWinnerScreen()
                        }
                    }
                }

                updateRoomValuesOnScreen(room)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error reading room values", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    private fun startPlayerEventListener() {
       playerEventListener = GameViewModel.playerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return
                playerObject = snapshot.getValue(Player::class.java)!!

                updatePlayerUI(playerObject)

                if (GameViewModel.playerLost(playerObject)) {
                    GameViewModel.removePlayerFromRoom()
                    goToLooserScreen()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updatePlayerUI(playerObject: Player) {
        playerTotalSticksTextView.text = playerObject.totalSticks.toString()
        if (playerObject.played == false) {
            enableUI()
        } else {
            disableUI()
        }

        if (playerObject.totalSticks == 0) { // Player perdeu
            goToLooserScreen()
            GameViewModel.removePlayerFromRoom()
        }
        GameViewModel.isHost = playerObject.host == true
    }


    private fun enableUI() {
        playButton.text = "Play"
        playButton.isEnabled = true
    }

    private fun disableUI() {
        sticksToPlayEditText.text.clear()
        finalGuessEditText.text.clear()

        playButton.isEnabled = false
        playButton.text = "Waiting..."
    }

    private fun goToLooserScreen() {
        if(GameViewModel.inGame) {
            GameViewModel.inGame = false
            removeCurrentRoomFromCache()
            startActivity(Intent(this@GameActivity, LooserActivity::class.java))
            finishActivity()
        }
    }

    private fun goToWinnerScreen() {
        if(GameViewModel.inGame){
            GameViewModel.inGame = false
            removeCurrentRoomFromCache()
            startActivity(Intent(this@GameActivity, WinnerActivity::class.java))
            finishActivity()
        }
    }

    private fun goToLobbyScreen() {
        removeCurrentRoomFromCache()
        startActivity(Intent(this@GameActivity, LobbyActivity::class.java))
        finishActivity()
    }

    private fun updateRoomValuesOnScreen(room: Room) {
        roundTextView.text = room.currentRound.toString() + "/" + room.maxRounds.toString()
        totalSticksInRoomTextview.text = GameViewModel.getTotalSticks(room.players).toString()
        lastRoundStickSumTextView.text = room.lastRoundSticks.toString()
    }

    private fun getPlayerNameFromCache(): String {
        val preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("playerName", "").toString()
    }

    private fun getCurrentRoomFromCache(): String {
        val preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("roomName", "").toString()
    }

    private fun removeCurrentRoomFromCache() {
        val preferences: SharedPreferences = getSharedPreferences(
            "PREFS",
            0
        )
        val editor = preferences.edit()
        editor.putString("roomName", "")
        editor.apply()
    }

    private fun finishActivity(){
        if (playerEventListener!=null){
            GameViewModel.playerRef.removeEventListener(playerEventListener)
        }
        if (playersListEventListener!=null){
            GameViewModel.playerRef.removeEventListener(playersListEventListener)
        }
        if (roomEventListener!=null){
            GameViewModel.playerRef.removeEventListener(roomEventListener)
        }
        finish()
    }
}