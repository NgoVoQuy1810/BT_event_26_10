package com.example.bt_event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bt_event.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fullName = intent.getStringExtra("FULL_NAME") ?: ""
        val email = intent.getStringExtra("EMAIL") ?: ""
        val phone = intent.getStringExtra("PHONE") ?: ""
        val gender = intent.getStringExtra("GENDER") ?: ""
        val socials = intent.getStringExtra("SOCIALS") ?: ""

        binding.tvResultDetail.text = """
            Họ và tên: $fullName
            Email: $email
            Số điện thoại: $phone
            Giới tính: $gender
            Mạng xã hội: $socials
        """.trimIndent()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}