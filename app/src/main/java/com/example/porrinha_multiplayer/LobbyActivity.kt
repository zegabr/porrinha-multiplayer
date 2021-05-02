package com.example.porrinha_multiplayer

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.porrinha_multiplayer.databinding.ActivityLobbyBinding
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.model.User
import com.example.porrinha_multiplayer.viewHolder.RoomsAdapter
import com.example.porrinha_multiplayer.viewModel.GameViewModel
import com.example.porrinha_multiplayer.viewModel.LobbyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LobbyActivity : AppCompatActivity() {

    lateinit var recyclerViewRooms: RecyclerView
    lateinit var button: Button
    lateinit var preferences: SharedPreferences
    lateinit var roomsList: MutableList<Room>
    lateinit var addRoomsListener: ValueEventListener
    var user: User = User("", 0.0, 0.0)
    var roomName = ""

    lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("PREFS", 0)
        user = getPlayerFromCache()
        roomName = getCurrentRoomFromCache()
        if (!roomName.equals("")) {
            goToGameScreen()
        }

        recyclerViewRooms = binding.roomsListRecycler
        button = binding.buttonCreateRoom

        roomsList = mutableListOf()// todas as rooms disponiveis

        addCreateRoomButtonOnClickListener() // ativa o botao de criar sala
        addRoomsEventListener() // ativa atualizaca da lista de salas
    }

    private fun finishActivity() {
        LobbyViewModel.roomsRef.removeEventListener(addRoomsListener)
        finish()
    }

    private fun addCreateRoomButtonOnClickListener() {
        button.setOnClickListener(View.OnClickListener {//Listener para o botão de criar sala
            val intent = Intent(this@LobbyActivity, CreateRoomActivity::class.java)
            intent.putExtra("name", user.username)
            intent.putExtra("latitude", user.latitude)
            intent.putExtra("longitude", user.longitude)
            startActivity(intent)
            finishActivity() // encerra activity atual
        })
    }

    private fun addRoomsEventListener() {
        LobbyViewModel.setRoomsReference()
        addRoomsListener =
            LobbyViewModel.roomsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // show list of rooms
                    roomsList.clear()
                    val rooms = snapshot.children
                    val recyclerViewRooms = binding.roomsListRecycler
                    for (room in rooms.iterator()) {
                        val actualRoom = room.getValue(Room::class.java)
                        if (actualRoom != null && actualRoom.currentRound == 1 && actualRoom.players!!.size < actualRoom.maxPlayers!!) {
                            roomsList.add(actualRoom) // adiciona todas as salas
                        }
                    }
                    recyclerViewRooms.apply {
                        layoutManager = LinearLayoutManager(this@LobbyActivity)
                        adapter = RoomsAdapter(roomsList, user, preferences, layoutInflater)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@LobbyActivity,
                        "Error reading list of rooms",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getPlayerFromCache(): User {
        val username = preferences.getString("playerName", "").toString()
        val latitude = preferences.getFloat("latitude", 0F).toDouble()
        val longitude = preferences.getFloat("longitude", 0F).toDouble()

        return User(username, latitude, longitude)
    }

    /**
     * Adiciona room atual no cache pra quando abrir o app de novo ja ir pra a tela de Game
     */
    private fun addCurrentRoomToCache() {
        val editor = preferences.edit()
        editor.putString("roomName", roomName) // seta o valor da sala
        editor.apply() // adicionou a sala na cache, qnd logar de novo ele vai estar lá
    }

    private fun getCurrentRoomFromCache(): String {
        return preferences.getString("roomName", "").toString()
    }

    private fun goToGameScreen() {
        addCurrentRoomToCache()
        val intent = Intent(this@LobbyActivity, GameActivity::class.java)
        startActivity(intent)
        finishActivity() // encerra activity atual
    }
}