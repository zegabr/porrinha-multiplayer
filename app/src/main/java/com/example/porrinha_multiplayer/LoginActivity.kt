package com.example.porrinha_multiplayer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.porrinha_multiplayer.databinding.ActivityLoginBinding
import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var editText : EditText
    lateinit var button: Button

    var user: String = "" // TODO: trocar pra uma classe com localizacao, username e senha, rank, etc
    lateinit var database : FirebaseDatabase
    lateinit var userRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editText = binding.editTextLogin
        button = binding.buttonLogin
        database = FirebaseDatabase.getInstance()

        // checks if user name exists and get reference
        user = getPlayerNameFromCache()
        if (!user.equals("")){ // TODO: mudar esse check pra null qnd trocar pleyer por um objeto
            // pega referencia do user no database
            userRef = database.getReference("users").child(user)
            addEventListener()
            userRef.setValue("") // TODO: passar o novo objeto aqui caso nao tenha nada em userRef
            // TODO: pra o eventListener ser triggado e ele mudar de activity, vai precisar mudar algo no objeto dessa ref. Aparentemente colocar msma coisa que ja existe nao serve
            // TODO: sugestao: colocar no objeto uma variavel lastLoginTime e dar update antes desse setValue(user), isso vai trigar a mudanca de activity
        }

        /**
         * Logging the user in
         */
        button.setOnClickListener {
            user = editText.text.toString()
            editText.text.clear()
            if (!user.equals("")) {
                button.setText("LOGGING IN")
                button.isEnabled = false

                userRef = database.getReference("users").child(user) // pega referencia do user no database (users/{username})
                addEventListener()
                userRef.setValue("") // TODO: ver o todo no setValue de cima
            }
        }
    }

    /**
     * Adiciona um listener que checa se a referencia pra o user atual foi modificada
     */
    private fun addEventListener() {
        // read from database
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!user.equals("")) { // TODO: mudar esse check pra null qnd trocar user por um objeto

                    addCurrentPlayerToCache()

                    // chama a proxima tela
                    startActivity(Intent(this@LoginActivity, LobbyActivity::class.java))
                    // encerra activity atual
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                button.setText("LOG IN")
                button.isEnabled = true
                Toast.makeText(applicationContext, "Error Logging in", Toast.LENGTH_SHORT).show()
            }
        })

    }

    /**
     * Adiciona user atual no cache pra quando abrir o app de novo nao precisar logar
     */
    private fun addCurrentPlayerToCache() {
        val preferences: SharedPreferences = getSharedPreferences(
            "PREFS",
            0
        )
        val editor = preferences.edit()
        editor.putString("user", user) // seta o valor user
        editor.apply() // adicionou o user na cache, qnd logar de novo ele vai estar lá
    }

    /**
     * pega o ultimo user logado no device
     */
    private fun getPlayerNameFromCache(): String {
        val preferences : SharedPreferences = getSharedPreferences("PREFS", 0)
        return preferences.getString("user", "").toString()
    }
}