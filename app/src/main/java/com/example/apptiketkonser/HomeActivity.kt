package com.example.apptiketkonser

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var _rvOnGoingConcert : RecyclerView
    private lateinit var _rvUpComingConcert : RecyclerView

    val db = Firebase.firestore
    var listOnGoingConcert = ArrayList<Concert>()
    var listUpComingConcert = ArrayList<Concert>()
    val sdf = SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _rvOnGoingConcert = findViewById(R.id.rvOnGoingConcert)
        _rvUpComingConcert = findViewById(R.id.rvUpComingConcert)

        loadData()
//        tampilkanData()
    }

    fun readData(db: FirebaseFirestore, onComplete: () -> Unit) {
        db.collection("tbConcert")
            .get()
            .addOnSuccessListener { result ->
                listOnGoingConcert.clear()
                listUpComingConcert.clear()
                for (document in result) {
                    val startPreOrderDate = document.data["StartPreOrderDate"] as Timestamp
                    val endPreOrderDate = document.data["EndPreOrderDate"] as Timestamp
                    val startConcertDate = document.data["StartConcertDate"] as Timestamp

                    val formattedStartPreOrderDate = sdf.format(startPreOrderDate.toDate())
                    val formattedEndPreOrderDate = sdf.format(endPreOrderDate.toDate())
                    val formattedStartConcertDate = sdf.format(startConcertDate.toDate())
                    val concert = Concert(
                        1,
                        document.data["Name"].toString(),
                        formattedStartPreOrderDate,
                        formattedEndPreOrderDate,
                        formattedStartConcertDate,
                        document.data["Description"].toString(),
                        document.data["ImageUrl"].toString(),
                        document.data["Price"].toString().toInt(),
                        document.data["NumberOfTickets"].toString().toInt(),
                        document.data["Venue"].toString()
                    )
                    if (startPreOrderDate.toDate().after(Date())) {
                        // Upcoming concert: Pre-order belum dimulai
                        listUpComingConcert.add(concert)
                    } else if (startPreOrderDate.toDate().before(Date()) && endPreOrderDate.toDate().after(Date())) {
                        // Ongoing concert: Pre-order sedang berlangsung
                        listOnGoingConcert.add(concert)
                    }
                }
                onComplete()
                Log.d("MainActivity Firebase", "Success")
            }
            .addOnFailureListener {
                Log.d("MainActivity Firebase", "Error getting documents: ", it)
            }
    }

    fun loadData(){
        readData(db){
            tampilkanData()
        }
    }

    fun tampilkanData(){
        _rvOnGoingConcert.layoutManager = LinearLayoutManager(this)
        _rvUpComingConcert.layoutManager = LinearLayoutManager(this)

        _rvOnGoingConcert.adapter = AdapterRecViewConcert(listOnGoingConcert)
        _rvUpComingConcert.adapter = AdapterRecViewConcert(listUpComingConcert)
    }

}