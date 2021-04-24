package com.example.porrinha_multiplayer

import android.R
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.porrinha_multiplayer.databinding.ActivityLobbyBinding
import com.example.porrinha_multiplayer.model.Player
import com.example.porrinha_multiplayer.viewModel.GameViewModel
import com.example.porrinha_multiplayer.viewModel.LobbyViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LobbyActivity : AppCompatActivity() {

    lateinit var listView : ListView
    lateinit var button: Button

    lateinit var roomsList: MutableList<String>

    var playerName = ""
    var roomName = ""

    lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerName = getPlayerNameFromCache()
        roomName = playerName

        listView = binding.listView // TODO: trocar pra recycleview no futuro distante
        button = binding.buttonCreateRoom

        roomsList = mutableListOf<String>()// todas as rooms disponiveis

        addCreateRoomButtonOnClickListener() // ativa o botao de criar sala
        addListViewOnItemClickListener() // ativa a interacao com itens da lista
        addRoomsEventListener() // ativa atualizaca da lista de salas
    }

    private fun addCreateRoomButtonOnClickListener() {
        button.setOnClickListener(View.OnClickListener {
            button.setText("CREATING ROOM")
            button.isEnabled = false

            // cria sala e adiciona o user como um player novo
            roomName = playerName
            LobbyViewModel.setRoomReference(roomName)
            GameViewModel.setPlayerReference(roomName, playerName)
            addRoomEventListener()
            GameViewModel.setPlayerReferenceValue(Player(playerName, 0, 3, false, true)) // adiciona o player na sala como host
        })
    }

    private fun addListViewOnItemClickListener() {
        listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            // join a room
            roomName = roomsList[position]
            GameViewModel.setPlayerReference(roomName, playerName)
            addRoomEventListener() // escuta updates na sala


            if(playerName.equals(roomName)){ // TODO: remover esse if, deixar só o setvalue, e salvar a room no cache (da msma forma q o login é salvo)
                GameViewModel.setPlayerReferenceValue(Player(playerName, 0, 3, false, true))
            }else{
                GameViewModel.setPlayerReferenceValue(Player(playerName, 0, 3, false, false)) //rooms/{roomName}/players/playerName = {objeto qqr} ==> ISSO TRIGGA O addRoomEventListener.onDataChange
            }
        })
    }

    private fun addRoomsEventListener(){
        LobbyViewModel.setRoomsReference()
        LobbyViewModel.roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // show list of rooms
                roomsList.clear()
                val rooms = snapshot.children

                for (room in rooms){
                    roomsList.add(room.key.toString()) // adiciona toda key de rooms/
                }
                listView.adapter = ArrayAdapter(this@LobbyActivity, R.layout.simple_list_item_1, roomsList) // atualiza o listview através desse adapter, mostrando as strings em roomsList
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addRoomEventListener() {
        GameViewModel.playerRef.addValueEventListener(object : ValueEventListener{
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
    private fun getPlayerNameFromCache(): String { // TODO: pegar objeto inteiro do User?
        val preferences : SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("playerName", "").toString()
    }

    private fun enableCreateButton() {
        button.setText("CREATE ROOM")
        button.isEnabled = true
    }

    private fun goToGameScreen() {
        val intent = Intent(this@LobbyActivity, GameActivity::class.java)
        intent.putExtra("roomName", roomName)
        startActivity(intent)
        finish() // encerra activity atual
    }
}