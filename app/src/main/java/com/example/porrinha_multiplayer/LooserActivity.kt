package com.example.porrinha_multiplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.porrinha_multiplayer.databinding.ActivityLooserBinding

class LooserActivity : AppCompatActivity() {
    lateinit var binding: ActivityLooserBinding
    lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLooserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backButton = binding.goBackbutton

        backButton.setOnClickListener {
            startActivity(Intent(this@LooserActivity, LobbyActivity::class.java))
            finish()
        }
    }
}