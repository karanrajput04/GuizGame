package com.karan.quizgame.login

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.karan.quizgame.MainActivity
import com.karan.quizgame.R
import com.karan.quizgame.databinding.ActivityLoginBinding
import com.karan.quizgame.signup.SignupActivity
import kotlinx.coroutines.MainScope

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding : ActivityLoginBinding
    var auth : FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var googleSignInClient : GoogleSignInClient

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        val textOfGoogleButton  = loginBinding.buttonGoogleSignin.getChildAt(0) as TextView
        textOfGoogleButton.text = "Continue with Google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        textOfGoogleButton.textSize = 16F

        /* Register Activity Launcher*/
        registerActivityForGoogleSignIn()

        /* Sign-in */
        loginBinding.buttonSingin.setOnClickListener {
            val userEmail = loginBinding.editTextLoginEmail.text.toString()
            val userPassword = loginBinding.editTextLoginPassword.text.toString()

            signUser(userEmail, userPassword)

        }
        /* Google Sign-in */
        loginBinding.buttonGoogleSignin.setOnClickListener {
            signInGoogle()
        }
        /* Sign-up*/
        loginBinding.textViewSingup.setOnClickListener {

            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)

        }
        /* Forgot Password*/
        loginBinding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    fun signUser(userEmail:String, userPassword:String){
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener {task ->
            if(task.isSuccessful){
                Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()
                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun signInGoogle() {
        val googleSigninOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.googleAPIKey))// this ID found in res (generated)
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSigninOption)

        signIn()
    }

    private fun signIn(){
        val signInIntent :Intent = googleSignInClient.signInIntent

        activityResultLauncher.launch(signInIntent)



    }

    private fun registerActivityForGoogleSignIn(){
        Log.i("MyLogs","inside registerActivityForGoogleSignIn")
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {result ->

                val resultCode = result.resultCode
                val data = result.data

                Log.i("MyLogs","Result Code $resultCode and Data $data")
                if(resultCode == RESULT_OK && data != null){
                    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSingInWithGoogle(task)
                }else{
                    //Toast.makeText(applicationContext, result.localizedMessage, Toast.LENGTH_SHORT).show()
                    //Log.i("MyLogs","inside registerActivityForGoogleSignIn-END")
                }
        })
        Log.i("MyLogs","inside registerActivityForGoogleSignIn-END")
    }

    private fun firebaseSingInWithGoogle(task: Task<GoogleSignInAccount>) {
        Log.i("MyLogs","inside firebaseSingInWithGoogle")
        try {
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)

            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        }catch (e : ApiException){
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        Log.i("MyLogs","inside firebaseSingInWithGoogle-END")
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        Log.i("MyLogs","inside firebaseGoogleAccount")
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task->
            if(task.isSuccessful){
                //val user = auth.currentUser
                // you get users account all details here like name, email, photo
            }else{

            }
        }
        Log.i("MyLogs","inside firebaseGoogleAccount - End")
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user != null){
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}