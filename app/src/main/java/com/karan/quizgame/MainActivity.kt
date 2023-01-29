package com.karan.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.karan.quizgame.databinding.ActivityLoginBinding
import com.karan.quizgame.databinding.ActivityMainBinding
import com.karan.quizgame.login.LoginActivity
import com.karan.quizgame.quiz.QuizActivity

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding : ActivityMainBinding

    var auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val  view = mainBinding.root
        setContentView(view)


        mainBinding.buttonStartQuiz.setOnClickListener {

            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)

        }

        mainBinding.buttonSignout.setOnClickListener {
            //email and password signout only
            auth.signOut()

            //gmail signout
            val gso =  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "Sign out is Successful", Toast.LENGTH_SHORT).show()
                }
            }


            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}