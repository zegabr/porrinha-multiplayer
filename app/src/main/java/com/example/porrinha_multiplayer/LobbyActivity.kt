package com.example.porrinha_multiplayer

import android.R
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.porrinha_multiplayer.databinding.ActivityLobbyBinding
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.model.Room
import com.example.porrinha_multiplayer.model.User
import com.example.porrinha_multiplayer.viewModel.GameViewModel
import com.example.porrinha_multiplayer.viewModel.LobbyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class LobbyActivity : AppCompatActivity() {

    lateinit var listView: ListView
    lateinit var button: Button

    lateinit var roomsList: MutableList<String>

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

        listView = binding.listView // TODO: trocar pra recycleview no futuro distante
        button = binding.buttonCreateRoom

        roomsList = mutableListOf()// todas as rooms disponiveis

        addCreateRoomButtonOnClickListener() // ativa o botao de criar sala
        addListViewOnItemClickListener() // ativa a interacao com itens da lista
        addRoomsEventListener() // ativa atualizaca da lista de salas
    }

    private fun addCreateRoomButtonOnClickListener() {
        button.setOnClickListener(View.OnClickListener {
            button.setText("CREATING ROOM")
            button.isEnabled = false

            // cria sala e adiciona o user como um player novo
            roomName = user.username!!
            LobbyViewModel.setRoomReference(roomName)
            LobbyViewModel.initRoom(user.latitude!!, user.longitude!!)
            GameViewModel.setPlayerReference(roomName, user.username!!)
            addRoomEventListener()
            GameViewModel.setPlayerReferenceValue(Player(user.username, 0, 3, false, true, true)) // adiciona o player na sala como host
        })
    }

    private fun addListViewOnItemClickListener() {
        listView.setOnItemClickListener { parent, view, position, id ->
            // join a room
            roomName = roomsList[position]
            GameViewModel.setPlayerReference(roomName, user.username!!)
            addRoomEventListener() // escuta updates na sala
            GameViewModel.setPlayerReferenceValue(Player(user.username!!, 0, 3, false, false, true)) //rooms/{roomName}/players/playerName = {objeto qqr} ==> ISSO TRIGGA O addRoomEventListener.onDataChange
        }
    }

    private fun addRoomsEventListener() {
        LobbyViewModel.setRoomsReference()
        LobbyViewModel.roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // show list of rooms
                roomsList.clear()
                val rooms = snapshot.children
                for (room in rooms.iterator()) {
                    var actualRoom = room.getValue(Room::class.java)
                    roomsList.add(room.key.toString()) // adiciona toda key de rooms/
                }
                listView.adapter = ArrayAdapter(this@LobbyActivity, R.layout.simple_list_item_1, roomsList) // atualiza o listview através desse adapter, mostrando as strings em roomsList
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LobbyActivity, "Error reading list of rooms", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addRootEventListener() {
        LobbyViewModel.setRootReference()
        LobbyViewModel.rootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

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
    private fun getPlayerFromCache(): User { // TODO: pegar objeto inteiro do User?
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