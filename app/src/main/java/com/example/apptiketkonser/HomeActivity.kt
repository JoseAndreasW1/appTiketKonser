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
    private var arConcert = ArrayList<Concert>()
    private lateinit var _rvConcert : RecyclerView

    val db = Firebase.firestore
    var listConcert = ArrayList<Concert>()
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

        _rvConcert = findViewById(R.id.rvConcert)

        loadData()
//        tampilkanData()
    }

    fun readData(db: FirebaseFirestore, onComplete: () -> Unit) {
        db.collection("tbConcert")
            .get()
            .addOnSuccessListener { result ->
                listConcert.clear()
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
                    listConcert.add(concert)
                }
                onComplete()
                Log.d("MainActivity Firebase", "Success ")
            }
            .addOnFailureListener {
                Log.d("MainActivity Firebase", "Error getting documents: ", it)
            }
    }

    fun loadData(){
        arConcert.clear()

        readData(db){
            arConcert.addAll(listConcert)
            tampilkanData()
        }
    }

    fun tampilkanData(){
        _rvConcert.layoutManager = LinearLayoutManager(this)

        val adapterTask = AdapterRecViewConcert(arConcert)
        _rvConcert.adapter = adapterTask
    }

}