package com.example.bt_event

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.bt_event.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastClickTime: Long = 0L
    private val CLICK_DEBOUNCE_DELAY = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEditTextEvents()
        setupRadioGroupEvent()
        setupCheckBoxEvents()
        setupSubmitButton()
    }

    // 1. Sự kiện cho các EditText (Xóa lỗi thời gian thực + báo Toast khi nhập xong/đổi focus)
    private fun setupEditTextEvents() {
        binding.etFullName.doAfterTextChanged {
            binding.tilFullName.error = null
        }
        binding.etFullName.setOnFocusChangeListener { _, hasFocus ->
            val text = binding.etFullName.text.toString().trim()
            if (!hasFocus && text.isNotEmpty()) {
                Toast.makeText(this, "Đã nhập Họ tên: $text", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etEmail.doAfterTextChanged {
            binding.tilEmail.error = null
        }
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            val text = binding.etEmail.text.toString().trim()
            if (!hasFocus && text.isNotEmpty()) {
                Toast.makeText(this, "Đã nhập Email: $text", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etPhone.doAfterTextChanged {
            binding.tilPhone.error = null
        }
        binding.etPhone.setOnFocusChangeListener { _, hasFocus ->
            val text = binding.etPhone.text.toString().trim()
            if (!hasFocus && text.isNotEmpty()) {
                Toast.makeText(this, "Đã nhập SĐT: $text", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etPassword.doAfterTextChanged {
            binding.tilPassword.error = null
        }
    }

    // 2. Sự kiện cho RadioButton Giới tính -> Hiện Toast thông báo
    private fun setupRadioGroupEvent() {
        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            val gender = when (checkedId) {
                R.id.rbMale -> "Nam"
                R.id.rbFemale -> "Nữ"
                else -> "Khác"
            }
            Toast.makeText(this, " Đã chọn giới tính: $gender", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Sự kiện cho các CheckBox Mạng xã hội -> Hiện Toast thông báo
    private fun setupCheckBoxEvents() {
        val checkListener = { cb: CheckBox, name: String ->
            cb.setOnCheckedChangeListener { _, isChecked ->
                val status = if (isChecked) "Đã chọn" else "Bỏ chọn"
                Toast.makeText(this, "$status mạng xã hội: $name", Toast.LENGTH_SHORT).show()
            }
        }
        checkListener(binding.cbFacebook, "Facebook")
        checkListener(binding.cbZalo, "Zalo")
        checkListener(binding.cbTiktok, "TikTok")
    }

    // 4. Sự kiện nút Đăng ký (Có chống click đúp + Validate + Chuyển màn hình)
    private fun setupSubmitButton() {
        binding.btnRegister.setOnClickListener {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime < CLICK_DEBOUNCE_DELAY) {
                return@setOnClickListener
            }
            lastClickTime = currentTime

            if (validateForm()) {
                val fullName = binding.etFullName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()

                val gender = when (binding.rgGender.checkedRadioButtonId) {
                    R.id.rbMale -> "Nam"
                    R.id.rbFemale -> "Nữ"
                    else -> "Khác"
                }

                val selectedSocials = mutableListOf<String>()
                if (binding.cbFacebook.isChecked) selectedSocials.add("Facebook")
                if (binding.cbZalo.isChecked) selectedSocials.add("Zalo")
                if (binding.cbTiktok.isChecked) selectedSocials.add("TikTok")
                val socialsText = if (selectedSocials.isEmpty()) "Không chọn" else selectedSocials.joinToString(", ")

                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                // Chuyển sang màn hình chi tiết
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("FULL_NAME", fullName)
                    putExtra("EMAIL", email)
                    putExtra("PHONE", phone)
                    putExtra("GENDER", gender)
                    putExtra("SOCIALS", socialsText)
                }
                startActivity(intent)
            }
        }
    }

    // Kiểm tra hợp lệ toàn bộ form theo chuẩn Lab 5
    private fun validateForm(): Boolean {
        var isValid = true

        val name = binding.etFullName.text.toString().trim()
        if (!FormValidator.isValidName(name)) {
            binding.tilFullName.error = "Họ tên tối thiểu 3 ký tự"
            isValid = false
        }

        val email = binding.etEmail.text.toString().trim()
        if (!FormValidator.isValidEmail(email)) {
            binding.tilEmail.error = "Email không đúng định dạng"
            isValid = false
        }

        val phone = binding.etPhone.text.toString().trim()
        if (!FormValidator.isValidPhone(phone)) {
            binding.tilPhone.error = "SĐT phải gồm 10 số (đầu 03, 05, 07, 08, 09)"
            isValid = false
        }

        val pass = binding.etPassword.text.toString()
        if (!FormValidator.isValidPassword(pass)) {
            binding.tilPassword.error = "Mật khẩu tối thiểu 6 ký tự"
            isValid = false
        }

        return isValid
    }
}