package com.example.apptiketkonser

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user = getSharedPreferences("user", MODE_PRIVATE).getString("user", null)
        val db = Firebase.firestore
        var tbUser: DocumentReference? = null

        // Set notification alarm
        if (user != null) {
            db.collection("tbUser")
                .document(user)
                .collection("tbTransaction")
                .get()
                .addOnSuccessListener { query ->
                    for ((index, transaction) in query.withIndex()) {
                        db.collection("tbConcert").document(transaction.id).get().addOnSuccessListener { concert ->
                            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm a", Locale.ENGLISH)
                            val concertDate = concert.get("StartConcertDate") as Timestamp
                            val intent = Intent(this, NotificationReceiver::class.java).apply {
                                putExtra("title", "Concert reminder")
                                putExtra("message", "You have a ${concert.get("Name")} concert at ${
                                    sdf.format(concertDate.toDate())
                                }")
                                putExtra("concert_id", concert.id)
                            }
                            val pendingIntent = PendingIntent.getBroadcast(
                                this,
                                concert.id.hashCode(),
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            // cancel if the alarm exists
                            alarmManager.cancel(pendingIntent)
                            val timer = 120000
                            Log.i("DataTimer", timer.toString())
                            // testing 2 mnt dri skrg
//                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timer, pendingIntent)

                            // set time 2 hours prior
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, concertDate.toDate().time - (2 * 60 * 60 * 1000), pendingIntent)
                        }
                    }
                }
        }

        //Gradient logo VibeTix
        val _tvGradient = findViewById<TextView>(R.id.tvGradient)
        val paint = _tvGradient.paint
        val width = paint.measureText(_tvGradient.text.toString())
        val shader = LinearGradient(
            0f, 0f, width, _tvGradient.textSize,
            intArrayOf(
                Color.parseColor("#980674"),
                Color.parseColor("#7C3AED")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        _tvGradient.invalidate()

        val _tabHome = findViewById<Button>(R.id.tabHome)
        val _tabTickets = findViewById<Button>(R.id.tabTickets)

        val _btnSignOut = findViewById<Button>(R.id.btnSignOut)

        _btnSignOut.setOnClickListener {
            val sp = getSharedPreferences("user", MODE_PRIVATE)
            val editor = sp.edit()

            if (editor != null) {
                editor.clear()
                editor.apply()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        _tabTickets.setOnClickListener {
            _tabHome.setBackgroundResource(android.R.color.transparent)
            _tabHome.setTextColor(resources.getColor(R.color.black))
            _tabTickets.setBackgroundResource(R.drawable.gradient_purplebutton)
            _tabTickets.setTextColor(resources.getColor(R.color.white))

            val mFragmentManager = supportFragmentManager
            val mfMyTickets = fMyTickets()
            mFragmentManager.findFragmentByTag(fMyTickets::class.java.simpleName)
            mFragmentManager
                .beginTransaction()
                .replace(
                    R.id.frameContainer,
                    mfMyTickets,
                    fMyTickets::class.java.simpleName)
                .commit()
        }

        _tabHome.setOnClickListener {
            _tabTickets.setBackgroundResource(android.R.color.transparent)
            _tabTickets.setTextColor(resources.getColor(R.color.black))
            _tabHome.setBackgroundResource(R.drawable.gradient_purplebutton)
            _tabHome.setTextColor(resources.getColor(R.color.white))

            val mFragmentManager = supportFragmentManager
            val mfListConcert = fListConcert()
            mFragmentManager.findFragmentByTag(fListConcert::class.java.simpleName)
            mFragmentManager
                .beginTransaction()
                .replace(
                    R.id.frameContainer,
                    mfListConcert,
                    fListConcert::class.java.simpleName)
                .commit()
        }

        //Initiate Fragment List Concert
        val mFragmentManager = supportFragmentManager
        val mfListConcert = fListConcert()

        mFragmentManager.findFragmentByTag(fListConcert::class.java.simpleName)
        mFragmentManager
            .beginTransaction()
            .add(R.id.frameContainer, mfListConcert, fListConcert::class.java.simpleName)
            .commit()
    }

    companion object {
        var idUser: String? = null
    }
}

