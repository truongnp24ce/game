package app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import app.AnimalVilla.R
import app.DTO.Player
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
    }

    fun registerUser(view: View) {
        val email: String = findViewById<EditText>(R.id.email_edit_text).text.toString().trim()
        val password: String = findViewById<EditText>(R.id.password_edit_text).text.toString()
        val rePassword: String = findViewById<EditText>(R.id.re_password_edit_text).text.toString()

        if (email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }
        if (password != rePassword) {
            Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    val uid = auth.currentUser?.uid.orEmpty()
                    if (uid.isNotEmpty()) {
                        val player = Player(status = 50, money = 50, energy = 50, uid = uid)
                        viewModel.save(player)
                    }
                    startActivity(Intent(this, TitleScreenActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                } else {
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun goToLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
