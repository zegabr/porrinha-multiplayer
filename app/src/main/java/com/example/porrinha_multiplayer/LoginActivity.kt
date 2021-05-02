package com.example.porrinha_multiplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.porrinha_multiplayer.databinding.ActivityLoginBinding
import com.example.porrinha_multiplayer.model.User
import com.example.porrinha_multiplayer.viewModel.LobbyViewModel
import com.example.porrinha_multiplayer.viewModel.LoginViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var editText: EditText
    lateinit var button: Button
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var user: User
    lateinit var location: Location
    lateinit var addUserRefListener: ValueEventListener
    var username: String = ""

    fun verifyPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun finishActivity() {
        if(addUserRefListener != null){
            LoginViewModel.userReference.removeEventListener(addUserRefListener)
        }
        finish()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editText = binding.editTextLogin
        button = binding.buttonLogin

        while (verifyPermission()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 200
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location
                } else {
                    this.location = Location("")
                    this.location.longitude = -34.5
                    this.location.latitude = -8.2
                }
            }

        // checks if user name exists and get reference
        user = getPlayerFromCache()
        if (!user.username.equals("")) {
            // pega referencia do user no database
            LoginViewModel.setupUserReference(username)
            addUserRefEventListener(LoginViewModel.userReference)
            LoginViewModel.setUserReferenceValue(
                User(
                    username,
                    location.latitude,
                    location.longitude
                )
            )
        }

        addLoginButtonClickListener()
    }

    private fun addLoginButtonClickListener() {
        button.setOnClickListener {
            username = editText.text.toString()
            editText.text.clear()
            if (!username.equals("")) {
                button.setText("LOGGING IN")
                button.isEnabled = false

                LoginViewModel.setupUserReference(username)
                addUserRefEventListener(LoginViewModel.userReference)
                LoginViewModel.setUserReferenceValue(
                    User(
                        username,
                        location.latitude,
                        location.longitude
                    )
                )
            }
        }
    }

    /**
     * Adiciona um listener que checa se a o user atual foi modificado no database
     */
    private fun addUserRefEventListener(userReference: DatabaseReference) {
        addUserRefListener = userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User = dataSnapshot.getValue(User::class.java)!!
                if (!user.username.equals("")) {
                    addCurrentPlayerToCache()
                    goToLobbyScreen() // chama a proxima tela
                }
            }

            override fun onCancelled(error: DatabaseError) {
                button.setText("LOG IN")
                button.isEnabled = true
                Toast.makeText(this@LoginActivity, "Error Logging in", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Vai pra o lobby
     */
    private fun goToLobbyScreen() {
        startActivity(Intent(this@LoginActivity, LobbyActivity::class.java))
        finishActivity()// encerra activity atual
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
        editor.putString("playerName", username) // seta o valor user
        editor.putFloat("latitude", location.latitude.toFloat())
        editor.putFloat("longitude", location.longitude.toFloat())
        editor.apply() // adicionou o username na cache, qnd logar de novo ele vai estar lá
    }

    /**
     * pega o ultimo user logado no device
     */
    private fun getPlayerFromCache(): User {
        val preferences: SharedPreferences = getSharedPreferences("PREFS", 0)
        username = preferences.getString("playerName", "").toString()
        val latitude = preferences.getFloat("latitude", 0F).toDouble()
        val longitude = preferences.getFloat("longitude", 0F).toDouble()
        if (!username.equals("")) {
            location = Location("")
            location.latitude = latitude
            location.longitude = longitude
        }
        return User(username, latitude, longitude)
    }
}