package com.example.apptiketkonser

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailConcertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_concert)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _ivImage = findViewById<ImageView>(R.id.ivImage)
        val _tvName = findViewById<TextView>(R.id.tvName)
        val _tvDescription = findViewById<TextView>(R.id.tvDescription)
        val _tvVenue = findViewById<TextView>(R.id.tvVenue)
        val _tvStartPreOrderDate = findViewById<TextView>(R.id.tvStartPreOrderDate)
        val _tvEndPreOrderDate = findViewById<TextView>(R.id.tvEndPreOrderDate)
        val _tvStartConcertDate = findViewById<TextView>(R.id.tvStartConcertDate)
        val _tvPrice = findViewById<TextView>(R.id.tvPrice)
        val _tvNumberOfTickets = findViewById<TextView>(R.id.tvNumberOfTickets)
        val _tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        val _btnBuyTicket = findViewById<Button>(R.id.btnBuyTicket)
        _btnBuyTicket.setOnClickListener {

        }

        Picasso.get().load(concert?.imageUrl).into(_ivImage)
        _tvName.text = concert?.name
        _tvDescription.text = concert?.description
        _tvVenue.text = concert?.venue
        _tvStartPreOrderDate.text = concert?.startPreOrderDate
        _tvEndPreOrderDate.text = concert?.endPreOrderDate
        _tvStartConcertDate.text = concert?.startConcertDate
        _tvPrice.text = "Rp. " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(concert?.price)
        //Tolong set Saldo di sini
        //_tvSaldo.text = "Saldo: Rp. "
        _tvNumberOfTickets.text = concert?.numberOfTickets.toString()

        //Hide button jika preorder belum dimulai
        //Kalau sudah pernah beli, dihide juga
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.ENGLISH)
        val parsedDate = sdf.parse(concert?.startPreOrderDate!!)
        if(parsedDate!!.after(Date())){
            _btnBuyTicket.visibility = Button.GONE
        }
    }

    companion object {
        var concert : Concert? = null
    }
}