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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private var arConcert = ArrayList<Concert>()
    private lateinit var _rvConcert : RecyclerView

    val db = Firebase.firestore
    var listConcert = ArrayList<Concert>()
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
        tampilkanData()
    }

    fun readData(db: FirebaseFirestore) {
        db.collection("tbConcert")
            .get()
            .addOnSuccessListener { result ->
                listConcert.clear()
                for (document in result) {
                    val concert = Concert(
                        document.data["id"].toString().toInt(),
                        document.data["Name"].toString(),
                        document.data["StartPreOrderDate"].toString(),
                        document.data["EndPreOrderDate"].toString(),
                        document.data["StartConcertDate"].toString(),
                        document.data["Description"].toString(),
                        document.data["ImageUrl"].toString(),
                        document.data["Price"].toString().toInt(),
                        document.data["NumberOfTickets"].toString().toInt()
                    )
                    listConcert.add(concert)
                }
            }
            .addOnFailureListener {
                Log.d("MainActivity Firebase", "Error getting documents: ", it)
            }
    }

    fun loadData(){
        arConcert.clear()

        readData(db)
        for (concert in listConcert){
            arConcert.add(concert)
        }
    }

    fun tampilkanData(){
        _rvConcert.layoutManager = LinearLayoutManager(this)

        val adapterTask = AdapterRecViewConcert(arConcert)
        _rvConcert.adapter = adapterTask
    }

}