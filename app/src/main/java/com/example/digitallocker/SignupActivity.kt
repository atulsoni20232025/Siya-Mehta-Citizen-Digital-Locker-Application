package com.example.digitallocker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var name: EditText
    lateinit var login: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        name = findViewById<EditText>(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)

        findViewById<Button>(R.id.sign).setOnClickListener {
            loginUser()
        }

        login.setOnClickListener {

            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun loginUser() {

        val userName = name.text.toString().trim()
        val userEmail = email.text.toString().trim()
        val userPassword = password.text.toString().trim()

        // 🔍 VALIDATIONS

        if (userName.isEmpty()) {
            name.error = "Enter Name"
            return
        }

        if (userEmail.isEmpty()) {
            email.error = "Enter Email"
            return
        }

        // Email format validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.error = "Enter valid email"
            return
        }

        if (userPassword.isEmpty()) {
            password.error = "Enter Password"
            return
        }

        if (userPassword.length < 8) {
            password.error = "Password must be at least 8 characters"
            return
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnSuccessListener {
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Signup Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}