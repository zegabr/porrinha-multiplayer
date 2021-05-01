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

    lateinit var roomsList: MutableList<Room>

    var user: User = User("",0.0,0.0)
    var roomName = ""

    lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = getPlayerFromCache()
        roomName = getCurrentRoomFromCache()
        if (!roomName.equals("")) {
            // vai direto pra o jogo
            goToGameScreen()
        }

        recyclerViewRooms = binding.roomsListRecycler
        button = binding.buttonCreateRoom

        roomsList = mutableListOf()// todas as rooms disponiveis

        addCreateRoomButtonOnClickListener() // ativa o botao de criar sala
        addRoomsEventListener() // ativa atualizaca da lista de salas
    }

    private fun addCreateRoomButtonOnClickListener() {
        button.setOnClickListener(View.OnClickListener {
            button.setText("CREATING ROOM")
            button.isEnabled = false

            // cria sala e adiciona o user como um player novo
            roomName = user.username!!
            LobbyViewModel.setRoomReference(roomName)
            LobbyViewModel.initRoom(user.latitude!!, user.longitude!!, user.username!!)
            GameViewModel.setPlayerReference(roomName, user.username!!)
            addRoomEventListener()
            GameViewModel.setPlayerReferenceValue(Player(user.username, 0, 3, false, true, true)) // adiciona o player na sala como host
        })
    }

    private fun addRoomsEventListener() {
        LobbyViewModel.setRoomsReference()
        LobbyViewModel.roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // show list of rooms
                roomsList.clear()
                val rooms = snapshot.children
                val recyclerViewRooms = binding.roomsListRecycler
                for (room in rooms.iterator()) {
                    var actualRoom = room.getValue(Room::class.java)
                    if (actualRoom != null){
                        roomsList.add(actualRoom) // adiciona todas as salas
                    }
                }
                recyclerViewRooms.apply {
                    layoutManager = LinearLayoutManager(this@LobbyActivity)
                    adapter = RoomsAdapter(roomsList, user, layoutInflater)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LobbyActivity, "Error reading list of rooms", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addRoomEventListener() {
        GameViewModel.playerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // join room
                enableCreateButton()
                goToGameScreen()
            }

            override fun onCancelled(error: DatabaseError) {
                // error
                enableCreateButton()
                Toast.makeText(this@LobbyActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // TODO: esse nao consegui deixar esse metodo acessivel por varias classes, ainda
    private fun getPlayerFromCache(): User {
        val preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        var username = preferences.getString("playerName", "").toString()
        val latitude = preferences.getFloat("latitude", 0F).toDouble()
        val longitude = preferences.getFloat("longitude", 0F).toDouble()

        return User(username, latitude, longitude)
    }

    /**
     * Adiciona room atual no cache pra quando abrir o app de novo ja ir pra a tela de Game
     */
    private fun addCurrentRoomToCache() {
        val preferences: SharedPreferences = getSharedPreferences(
                "PREFS",
                0
        )
        val editor = preferences.edit()
        editor.putString("roomName", roomName) // seta o valor user
        editor.apply() // adicionou o username na cache, qnd logar de novo ele vai estar lá
    }

    private fun getCurrentRoomFromCache(): String {
        val preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("roomName", "").toString()
    }

    private fun enableCreateButton() {
        button.setText("CREATE ROOM")
        button.isEnabled = true
    }

    private fun goToGameScreen() {
        addCurrentRoomToCache()
        val intent = Intent(this@LobbyActivity, GameActivity::class.java)
        intent.putExtra("roomName", roomName)
        startActivity(intent)
        finish() // encerra activity atual
    }
}