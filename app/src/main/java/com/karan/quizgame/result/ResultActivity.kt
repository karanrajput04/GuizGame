package com.karan.quizgame.result

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karan.quizgame.MainActivity
import com.karan.quizgame.R
import com.karan.quizgame.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    lateinit var resultBinding: ActivityResultBinding

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.getReference().child("scores")

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var userCorrect = ""
    var userWrong = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        val view = resultBinding.root
        setContentView(view)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user?.let {
                    val userUid = it.uid

                    userCorrect = snapshot.child(userUid).child("correct").value.toString()
                    userWrong = snapshot.child(userUid).child("wrong").value.toString()

                    resultBinding.textViewScoreCorrect.text = userCorrect
                    resultBinding.textViewScoreWrong.text = userWrong

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        resultBinding.buttonPlayAgain.setOnClickListener {
            val intent = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        resultBinding.buttonExit.setOnClickListener {
            finish()
        }


    }
}