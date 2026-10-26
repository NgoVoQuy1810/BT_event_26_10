package com.example.bt_event

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bt_event.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận đối tượng User từ Intent
        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("USER_DATA", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("USER_DATA") as? User
        }

        user?.let {
            binding.tvWelcome.text = "Xin chào, ${it.fullName}! 🎉"
            binding.tvUserInfo.text = """
                THÔNG TIN TÀI KHOẢN:
                ────────────────────────
                Họ và tên: ${it.fullName}
                Email: ${it.email}
                Số điện thoại: ${it.phone}
                Giới tính: ${it.gender}
            """.trimIndent()
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }
    }
}