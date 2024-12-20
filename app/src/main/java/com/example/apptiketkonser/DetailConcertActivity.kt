package com.example.apptiketkonser

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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso
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


        _tvName.text = concert?.name
        _tvDescription.text = concert?.description
        _tvVenue.text = concert?.venue
        _tvStartPreOrderDate.text = concert?.startPreOrderDate
        _tvEndPreOrderDate.text = concert?.endPreOrderDate
        _tvStartConcertDate.text = concert?.startConcertDate
        _tvPrice.text = "Rp. " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(concert?.price)
        _tvPrice2.text = "Rp. " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(concert?.price)
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