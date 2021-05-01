package com.example.porrinha_multiplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.porrinha_multiplayer.databinding.ActivityWinnerBinding

class WinnerActivity : AppCompatActivity() {
    lateinit var binding: ActivityWinnerBinding
    lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWinnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backButton = binding.goBackButton

        backButton.setOnClickListener {
            startActivity(Intent(this@WinnerActivity, LobbyActivity::class.java))
            finish()
        }
    }
}