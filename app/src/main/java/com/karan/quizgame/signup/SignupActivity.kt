package com.karan.quizgame.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.karan.quizgame.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var signupBinding: ActivitySignupBinding

    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        val view = signupBinding.root
        setContentView(view)
        signupBinding.buttonSignup.setOnClickListener { 
            val email = signupBinding.editTextSignupEmail.text.toString()
            val password = signupBinding.editTextSignupPassword.text.toString()
            signupWithEmailAndPassword(email, password)
        }



    }
    fun signupWithEmailAndPassword(email:String, password: String){
        signupBinding.progressBarSingup.visibility = View.VISIBLE
        signupBinding.buttonSignup.isClickable = false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(applicationContext, "Your account has been created!", Toast.LENGTH_SHORT).show()
                signupBinding.progressBarSingup.visibility = View.INVISIBLE
                signupBinding.buttonSignup.isClickable = true
                finish()
            }else{
                Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                signupBinding.progressBarSingup.visibility = View.INVISIBLE
                signupBinding.buttonSignup.isClickable = true
            }



        }
    }
}