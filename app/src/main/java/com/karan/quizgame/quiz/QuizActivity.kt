package com.karan.quizgame.quiz

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karan.quizgame.MainActivity
import com.karan.quizgame.R
import com.karan.quizgame.databinding.ActivityQuizBinding
import com.karan.quizgame.result.ResultActivity
import kotlin.concurrent.timer
import kotlin.random.Random


class QuizActivity : AppCompatActivity() {
    lateinit var quizBinding : ActivityQuizBinding

    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference.child("Questions")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount = 0
    var questionNumber = 0

    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0

    lateinit var timer : CountDownTimer
    private val totalTime = 25000L
    var timerContinue = false
    var leftTime = totalTime

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreRef = database.reference

    val questions = HashSet<Int>()

    val numberOfQuesInTest : Int= 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        do {
            val number = Random.nextInt(1, 11)
            questions.add(number)
        }while (questions.size < numberOfQuesInTest)

        gameLogic()

        quizBinding.linearLayoutInfo.visibility = View.INVISIBLE
        quizBinding.linearLayoutQuestion.visibility = View.INVISIBLE
        quizBinding.linearLayoutButtons.visibility = View.INVISIBLE

        quizBinding.buttonNext.setOnClickListener {
            resetTimer()
            gameLogic()
        }

        quizBinding.buttonFinish.setOnClickListener {

            sendScore()

        }

        quizBinding.textViewA.setOnClickListener {
            userAnswer = "a"
            validateAnswer(userAnswer, quizBinding.textViewA)
            disableClickableOptions()
        }

        quizBinding.textViewB.setOnClickListener {
            userAnswer = "b"
            validateAnswer(userAnswer, quizBinding.textViewB)
            disableClickableOptions()
        }

        quizBinding.textViewC.setOnClickListener {
            userAnswer = "c"
            validateAnswer(userAnswer, quizBinding.textViewC)
            disableClickableOptions()
        }

        quizBinding.textViewD.setOnClickListener {
            userAnswer = "d"
            validateAnswer(userAnswer, quizBinding.textViewD)
            disableClickableOptions()
        }
    }
    private fun gameLogic(){

        restoreOptions()

        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                questionCount =  snapshot.childrenCount.toInt()
                Log.i("MyLogs", "Populating Question - $questionNumber - $questionCount")

                if(questionNumber < questions.size) {
                    val queNo = questions.elementAt(questionNumber).toString()
                    question = snapshot.child(queNo).child("q").value.toString()
                    answerA = snapshot.child(queNo).child("a").value.toString()
                    answerB = snapshot.child(queNo).child("b").value.toString()
                    answerC = snapshot.child(queNo).child("c").value.toString()
                    answerD = snapshot.child(queNo).child("d").value.toString()
                    correctAnswer = snapshot.child(queNo).child("answer").value.toString()

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewA.text = answerA
                    quizBinding.textViewB.text = answerB
                    quizBinding.textViewC.text = answerC
                    quizBinding.textViewD.text = answerD

                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestion.visibility = View.VISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE

                    startTimer()

                }else{
                    //Toast.makeText(applicationContext, "You answered all the questions!", Toast.LENGTH_SHORT).show()

                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle(R.string.app_name)
                    dialogMessage.setMessage("Congratulation!!!,\nYou have answered all the questions. Do you want to see the results?")
                    dialogMessage.setCancelable(false)

                    dialogMessage.setPositiveButton("See Result"){dialogWindow, postion->
                        sendScore()
                    }
                    dialogMessage.setNegativeButton("Play Again"){dialogWindow, postion->
                        val intent = Intent(this@QuizActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialogMessage.create().show()
                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }
    private fun validateAnswer(answer: String, textView : TextView){
        pauseTimer()
        if(correctAnswer == answer){
            textView.setBackgroundColor(Color.GREEN)
            userCorrect++
            quizBinding.textViewCorrent.text = userCorrect.toString()
        }else{
            textView.setBackgroundColor(Color.RED)
            userWrong++
            quizBinding.textViewWrong.text = userWrong.toString()
            findAnswer()
        }
    }

    private fun findAnswer(){
        when(correctAnswer){
            "a" -> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d" -> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }
    private  fun disableClickableOptions(){
        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false
    }

    private  fun restoreOptions(){
        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true

    }

    private fun startTimer(){
        timer = object: CountDownTimer(leftTime, 1000){
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                disableClickableOptions()
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Sorry, Time is up! Continue with next question"
                timerContinue = false
            }
        }.start()
        timerContinue = true
    }

    private fun updateCountDownText() {
        val remainingTime : Int = (leftTime / 1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()
    }
    private fun pauseTimer() {
        timer.cancel()
        timerContinue = false
    }
    private fun resetTimer() {
        pauseTimer()
        leftTime = totalTime
        updateCountDownText()
    }

    private fun sendScore(){
        user?.let {
            val userUid = it.uid
            scoreRef.child("scores").child(userUid).child("correct").setValue(userCorrect)
            scoreRef.child("scores").child(userUid).child("wrong").setValue(userWrong).addOnSuccessListener {
                Toast.makeText(applicationContext, "Scores sent to database successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@QuizActivity, ResultActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

}