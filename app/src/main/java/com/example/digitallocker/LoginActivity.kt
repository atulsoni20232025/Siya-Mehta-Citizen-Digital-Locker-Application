package com.example.digitallocker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var register: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.useremail)
        password = findViewById(R.id.userpassword)
        register = findViewById(R.id.newregister)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            loginUser()
        }

        register.setOnClickListener {

            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)

        }
    }

    private fun loginUser() {
        auth.signInWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        ).addOnSuccessListener {
            startActivity(Intent(this, MainActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }


}