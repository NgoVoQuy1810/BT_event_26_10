package com.example.bt_event

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.bt_event.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastClickTime: Long = 0L
    private val CLICK_DEBOUNCE_DELAY = 1000L

    // Biến lưu tạm để hỗ trợ nút HOÀN TÁC trên Snackbar khi bấm Hủy
    private var savedName = ""
    private var savedEmail = ""
    private var savedPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRealtimeValidation()
        setupRegisterButton()
        setupCancelButton()
    }

    // 1. Lắng nghe gõ phím thời gian thực để xóa lỗi tức thì (Bài 2 & Mở rộng 1 PDF)
    private fun setupRealtimeValidation() {
        binding.etFullName.doAfterTextChanged {
            if (binding.tilFullName.error != null) {
                binding.tilFullName.error = null
            }
        }
        binding.etEmail.doAfterTextChanged {
            if (binding.tilEmail.error != null) {
                binding.tilEmail.error = null
            }
        }
        binding.etPhone.doAfterTextChanged {
            if (binding.tilPhone.error != null) {
                binding.tilPhone.error = null
            }
        }
        binding.etPassword.doAfterTextChanged {
            if (binding.tilPassword.error != null) {
                binding.tilPassword.error = null
            }
        }
        binding.etConfirmPassword.doAfterTextChanged { text ->
            val pass = binding.etPassword.text.toString()
            val confirm = text.toString()
            if (confirm.isNotEmpty() && pass != confirm) {
                binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp!"
            } else {
                binding.tilConfirmPassword.error = null
            }
        }
    }

    // 2. Xử lý nút ĐĂNG KÝ (Fail -> Snackbar báo lỗi | Success -> Chuyển sang Trang chủ)
    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            // Chống click đúp (Debouncing - Bài 1 PDF)
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime < CLICK_DEBOUNCE_DELAY) {
                return@setOnClickListener
            }
            lastClickTime = currentTime

            if (validateForm()) {
                // Tạo đối tượng từ class User
                val gender = when (binding.rgGender.checkedRadioButtonId) {
                    R.id.rbMale -> "Nam"
                    R.id.rbFemale -> "Nữ"
                    else -> "Khác"
                }

                val newUser = User(
                    fullName = binding.etFullName.text.toString().trim(),
                    email = binding.etEmail.text.toString().trim(),
                    phone = binding.etPhone.text.toString().trim(),
                    gender = gender
                )

                // Chuyển sang màn hình Trang chủ hiển thị thông tin
                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("USER_DATA", newUser)
                }
                startActivity(intent)
            } else {
                // FAIL --> Hiện Snackbar báo lỗi
                val snackbar = Snackbar.make(
                    binding.root,
                    "Đăng ký thất bại! Vui lòng kiểm tra lại các trường báo đỏ.",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setBackgroundTint(Color.parseColor("#E74C3C"))
                snackbar.setTextColor(Color.WHITE)
                snackbar.setAction("ĐÓNG") { snackbar.dismiss() }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()
            }
        }
    }

    // 3. Xử lý nút HỦY --> Xóa trắng các trường để nhập lại
    private fun setupCancelButton() {
        binding.btnCancel.setOnClickListener {
            // Lưu tạm dữ liệu trước khi xóa để có thể Hoàn tác (Bài 5 PDF)
            savedName = binding.etFullName.text.toString()
            savedEmail = binding.etEmail.text.toString()
            savedPhone = binding.etPhone.text.toString()

            clearForm()

            val snackbar = Snackbar.make(
                binding.root,
                "Đã xóa trắng toàn bộ thông tin đã nhập",
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction("HOÀN TÁC") {
                restorePreviousFormData()
            }
            snackbar.setActionTextColor(Color.parseColor("#E74C3C"))
            snackbar.show()
        }
    }

    private fun clearForm() {
        binding.etFullName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPhone.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()

        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPhone.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        binding.rbMale.isChecked = true
        binding.cbTerms.isChecked = true

        // Đặt con trỏ chuột về lại ô đầu tiên
        binding.etFullName.requestFocus()
    }

    private fun restorePreviousFormData() {
        binding.etFullName.setText(savedName)
        binding.etEmail.setText(savedEmail)
        binding.etPhone.setText(savedPhone)
    }

    // 4. Hàm kiểm tra toàn bộ Form (Cumulative Validation + Auto Focus ô lỗi đầu tiên)
    private fun validateForm(): Boolean {
        var isValid = true
        var firstErrorView: View? = null

        val name = binding.etFullName.text.toString().trim()
        if (!FormValidator.isValidName(name)) {
            binding.tilFullName.error = "Họ tên không được để trống (tối thiểu 3 ký tự)"
            if (firstErrorView == null) firstErrorView = binding.etFullName
            isValid = false
        }

        val email = binding.etEmail.text.toString().trim()
        if (!FormValidator.isValidEmail(email)) {
            binding.tilEmail.error = "Email không đúng định dạng (VD: sv@ute.vn)"
            if (firstErrorView == null) firstErrorView = binding.etEmail
            isValid = false
        }

        val phone = binding.etPhone.text.toString().trim()
        if (!FormValidator.isValidPhone(phone)) {
            binding.tilPhone.error = "Số điện thoại không hợp lệ (10 số, đầu 03/05/07/08/09)"
            if (firstErrorView == null) firstErrorView = binding.etPhone
            isValid = false
        }

        val pass = binding.etPassword.text.toString()
        if (!FormValidator.isValidPassword(pass)) {
            binding.tilPassword.error = "Mật khẩu tối thiểu 6 ký tự"
            if (firstErrorView == null) firstErrorView = binding.etPassword
            isValid = false
        }

        val confirmPass = binding.etConfirmPassword.text.toString()
        if (!FormValidator.isConfirmPasswordMatch(pass, confirmPass)) {
            binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp!"
            if (firstErrorView == null) firstErrorView = binding.etConfirmPassword
            isValid = false
        }

        if (!binding.cbTerms.isChecked) {
            isValid = false
        }

        // Tự động nhảy con trỏ vào ô lỗi đầu tiên (BT Mở rộng 3 PDF)
        firstErrorView?.requestFocus()

        return isValid
    }
}