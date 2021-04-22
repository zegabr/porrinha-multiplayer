package com.example.porrinha_multiplayer

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.porrinha_multiplayer.databinding.ActivityGameBinding
import com.example.porrinha_multiplayer.databinding.ActivityLobbyBinding
import com.example.porrinha_multiplayer.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class LobbyActivity : AppCompatActivity() {

    lateinit var listView : ListView
    lateinit var button: Button

    lateinit var roomsList: MutableList<String>

    var playerName = ""
    var roomName = ""

    lateinit var database : FirebaseDatabase
    lateinit var playerInsideRoomRef : DatabaseReference
    lateinit var roomsRef : DatabaseReference
    lateinit var playersRef : DatabaseReference

    lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        playerName = getPlayerNameFromCache()
        roomName = playerName

        listView = binding.listView // TODO: trocar pra recycleview no futuro distante
        button = binding.buttonCreateRoom

        // todas as rooms disponiveis
        roomsList = mutableListOf<String>()

        button.setOnClickListener(View.OnClickListener {
            button.setText("CREATING ROOM")
            button.isEnabled = false

            // create room and add current user as player2
            roomName = playerName
            playersRef = database.getReference("rooms").child(roomName).child("players")
            playerInsideRoomRef = playersRef.child(playerName)
            addRoomEventListener()
            // TODO: trocar por um objeto Player (com as coisas necessariasa para um player no jogo)
            playerInsideRoomRef.setValue("oi sou o host pq a sala tem meu nome") // rooms/{roomName}/players/player1 = {playerName}
        })

        listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            // join a room
            roomName = roomsList[position]
            playersRef = database.getReference("rooms").child(roomName).child("players")
            playerInsideRoomRef = playersRef.child(playerName) // TODO: dar um jeito de pegar a referencia pra Player a partir dessa ref

            addRoomEventListener()

            if(playerName.equals(roomName)){ // TODO: atualizar algo do Player aqui, pra triggar o addRoomEventListener
                playerInsideRoomRef.setValue("oi sou o host pq a sala tem meu nome")
            }else{
                playerInsideRoomRef.setValue("oi sou um guest pq a sala nao tem meu nome") //rooms/{roomName}/players/playerName = {objeto qqr} ==> ISSO TRIGGA O addRoomEventListener.onDataChange
            }
        })

        // show if new rooms are available
        addRoomsEventListener()
    }

    private fun getPlayerNameFromCache(): String {
        val preferences : SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("playerName", "").toString()
    }

    private fun addRoomEventListener() {
        playerInsideRoomRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // join room
                button.setText("CREATE ROOM")
                button.isEnabled = true

                val intent = Intent(this@LobbyActivity, GameActivity::class.java)
                intent.putExtra("roomName", roomName)
                startActivity(intent)
                finish() // encerra activity atual
            }

            override fun onCancelled(error: DatabaseError) {
                // error
                button.setText("CREATE ROOM")
                button.isEnabled = true
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addRoomsEventListener(){
        roomsRef = database.getReference("rooms")
        roomsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // show list of rooms
                roomsList.clear()
                val rooms = snapshot.children

                for (room in rooms){
                    roomsList.add(room.key.toString()) // adiciona toda key de rooms/
                }
                listView.adapter = ArrayAdapter(this@LobbyActivity, android.R.layout.simple_list_item_1, roomsList) // atualiza o listview através desse adapter, mostrando as strings em roomsList
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}