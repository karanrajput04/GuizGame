package com.karan.quizgame.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.karan.quizgame.R
import com.karan.quizgame.databinding.ActivityForgotPasswordBinding
import com.karan.quizgame.databinding.ActivityMainBinding

class ForgotPasswordActivity : AppCompatActivity() {
    var auth : FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var forgotBinding : ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = forgotBinding.root
        setContentView(view)

        forgotBinding.buttonReset.setOnClickListener {
            val userEmail = forgotBinding.editTextForgotEmail.text.toString()
            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener {task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "We sent password reset mail to your email address!", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}