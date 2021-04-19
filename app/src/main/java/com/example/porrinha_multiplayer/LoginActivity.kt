package com.example.porrinha_multiplayer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {

    lateinit var editText : EditText
    lateinit var button: Button

    var playerName: String = ""

    lateinit var database : FirebaseDatabase
    lateinit var playerRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editText = findViewById(R.id.editTextLogin)
        button = findViewById(R.id.buttonLogin)

        database = FirebaseDatabase.getInstance()

        // checks if player name exists and get reference
        var preferences : SharedPreferences = getSharedPreferences("PREFS", 0) // pega as preferencias salvas na cache no diretorio PREFS/
        playerName = preferences.getString("playerName", "").toString() // se existir playername lá, pega
        if (!playerName.equals("")){
            playerRef = database.getReference("players/$playerName") // pega referencia do player no database
            addEventListener()
            playerRef.setValue("") // ainda nao sei oq isso faz
        }

        /**
         * Logging the player in
         */
        button.setOnClickListener(View.OnClickListener { v ->
            playerName = editText.text.toString()
            editText.text.clear()
            if (!playerName.equals("")) {
                button.setText("LOGGING IN")
                button.isEnabled = false

                playerRef = database.getReference("players/$playerName") // pega referencia do player no database
                addEventListener()
                playerRef.setValue("") // ainda nao sei oq isso faz
            }

        })
    }

    private fun addEventListener() {
        // read from database
        playerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!playerName.equals("")) {

                    val preferences: SharedPreferences = getSharedPreferences(
                        "PREFS",
                        0
                    ) // pega as preferencias salvas na cache no diretorio PREFS/
                    val editor = preferences.edit()
                    editor.putString("playerName", playerName) // seta o valor playerName
                    editor.apply()

                    val intent = Intent(this@LoginActivity, LobbyActivity::class.java)
                    startActivity(intent)
                    finish() // encerra activity atual
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                button.setText("LOG IN")
                button.isEnabled = true
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
        })

    }
}