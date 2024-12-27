package com.example.apptiketkonser

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DetailConcertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_detail_concert)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _ivImage = findViewById<ImageView>(R.id.poster)
        val _ivImage2 = findViewById<ImageView>(R.id.posterbackground)
        val _tvName = findViewById<TextView>(R.id.tvName)
        val _tvDescription = findViewById<TextView>(R.id.tvDescription)
        val _tvVenue = findViewById<TextView>(R.id.tvVenue)
        val _tvStartPreOrderDate = findViewById<TextView>(R.id.tvStartPreOrderDate)
        val _tvEndPreOrderDate = findViewById<TextView>(R.id.tvEndPreOrderDate)
        val _tvStartConcertDate = findViewById<TextView>(R.id.tvStartConcertDate)
        val _tvPrice = findViewById<TextView>(R.id.tvPrice)
        val _tvPrice2 = findViewById<TextView>(R.id.tvPrice2)
        val _tvNumberOfTickets = findViewById<TextView>(R.id.tvNumberOfTickets)
        val _tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        val _btnBuyTicket = findViewById<Button>(R.id.btnBuyTicket)
        _btnBuyTicket.setOnClickListener {

        }


        Picasso.get().load(concert?.imageUrl).into(_ivImage)
        Picasso.get().load(concert?.imageUrl).into(_ivImage2)

        // Get the bitmap from the ImageView (_ivImage2) after loading the image
        _ivImage2.post {
            // Decode the image into a Bitmap (once Picasso has loaded it into _ivImage2)
            val drawable = _ivImage2.drawable
            if (drawable is BitmapDrawable) {
                val originalBitmap = drawable.bitmap

                // Create a mutable copy of the original bitmap
                val blurredBitmap = Bitmap.createBitmap(
                    originalBitmap.width,
                    originalBitmap.height,
                    Bitmap.Config.ARGB_8888
                )

                // Initialize RenderScript and allocations
                val rs = RenderScript.create(this)
                val input = Allocation.createFromBitmap(rs, originalBitmap)
                val output = Allocation.createFromBitmap(rs, blurredBitmap)

                // Create the blur script and set the blur radius
                val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                blurScript.setRadius(25f) // Max blur radius is 25
                blurScript.setInput(input)
                blurScript.forEach(output)

                // Copy the blurred result to the output bitmap
                output.copyTo(blurredBitmap)

                // Set the blurred bitmap to the background ImageView
                _ivImage2.setImageBitmap(blurredBitmap)

                // Clean up RenderScript resources
                rs.destroy()
            }
        }
        // get user data
        val user = getSharedPreferences("user", MODE_PRIVATE).getString("user", null)
        val db = Firebase.firestore
        var tbUser: DocumentReference? = null
        if (user != null) {
            tbUser = db.collection("tbUser")
                .document(user)
        }

        _tvName.text = concert?.name
        _tvDescription.text = concert?.description
        _tvVenue.text = concert?.venue
        _tvStartPreOrderDate.text = concert?.startPreOrderDate
        _tvEndPreOrderDate.text = concert?.endPreOrderDate
        _tvStartConcertDate.text = concert?.startConcertDate
        _tvPrice.text =
            "Rp. " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(concert?.price)
        _tvPrice2.text =
            "Rp. " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(concert?.price)
        //Tolong set Saldo di sini
        if (tbUser != null) {
            tbUser.get().addOnSuccessListener { query ->
                _tvSaldo.text = "Rp. " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(query.get("saldo").toString().toInt())
            }
        }
        _tvNumberOfTickets.text = concert?.numberOfTickets.toString()

        //Hide button jika preorder belum dimulai
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.ENGLISH)
        val parsedDate = sdf.parse(concert?.startPreOrderDate!!)


        if (parsedDate!!.after(Date())) {
            _btnBuyTicket.visibility = Button.GONE
        } else {
            // Check if user already bought the ticket
            if (tbUser != null) {
                tbUser
                    .collection("tbTransaction")
                    .get()
                    .addOnSuccessListener { query ->
                        for (transaction in query.documents) {
                            Log.i("DataTransaction", (transaction.id == concert!!.id).toString())
                            if (concert != null) {
                                if (transaction.id == concert!!.id) {
                                    _btnBuyTicket.visibility = Button.GONE
                                }
                            }
                        }
                    }
            }
        }

        _btnBuyTicket.setOnClickListener {
            if (user != null) {
                db.runTransaction { transaction ->
                    if (tbUser != null) {
                        val dataUser = transaction.get(tbUser)
                        if (!dataUser.exists())
                            throw Exception("User doesn't exist, please try again later")

                        val currentSaldo = dataUser.get("saldo")
                        if (currentSaldo.toString().toInt() < concert!!.price)
                            throw Exception("Saldo tidak cukup")

                        val tbConcert = db.collection("tbConcert").document(concert!!.id)
                        val totalTicket = transaction.get(tbConcert).get("NumberOfTickets") as Long
                        if (totalTicket <= 0)
                            throw Exception("Tiket telah habis!")

                        transaction.update(
                            tbUser,
                            "saldo",
                            currentSaldo.toString().toInt() - concert!!.price
                        )

                        val tbTransaction =
                            tbUser.collection("tbTransaction").document(concert!!.id)
                        val purchaseDate = hashMapOf("PurchaseDate" to Date())

                        transaction.set(tbTransaction, purchaseDate)
                        transaction.update(tbConcert, "NumberOfTickets", totalTicket - 1)
                    } else {
                        throw Exception("User table not found")
                    }
                }
                    .addOnSuccessListener {
                        // set alarm
                        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm", Locale.ENGLISH)
                        val intent = Intent(this, NotificationReceiver::class.java).apply {
                            putExtra("title", "Concert reminder")
                            putExtra(
                                "message", "You have a ${concert?.name} concert on ${
                                    concert!!.startConcertDate
                                }"
                            )
                            putExtra("concert_id", concert?.id)
                        }
                        val pendingIntent = PendingIntent.getBroadcast(
                            this,
                            concert?.id.hashCode(),
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
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            sdf.parse(concert!!.startConcertDate).time - (2 * 60 * 60 * 1000),
                            pendingIntent
                        )

                        // success message
                        Toast.makeText(this, "Berhasil membeli tiket", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.i("ErrorTicket", exception.message.toString())
                        Toast.makeText(
                            this,
                            "Something went wrong, please try again later ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    companion object {
        var concert: Concert? = null
    }
}