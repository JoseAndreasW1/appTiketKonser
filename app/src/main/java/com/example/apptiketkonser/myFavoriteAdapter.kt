package com.example.apptiketkonser

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

class myFavoriteAdapter(
    private val favoriteList: MutableList<Favorite>

) : RecyclerView.Adapter<myFavoriteAdapter.MyFavoriteViewHolder>() {
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)

    class MyFavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.poster)
        val concertName: TextView = itemView.findViewById(R.id.concertName)
        val concertDate: TextView = itemView.findViewById(R.id.concertDate)
        val concertVenue :TextView = itemView.findViewById(R.id.concertVenue)
        val _btnUnlike = itemView.findViewById<ImageView>(R.id.btnUnlike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyler_favorite, parent, false)
        return MyFavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFavoriteViewHolder, position: Int) {
        val favorite = favoriteList[position]

        // Query tbconcert table to get concert details
        val db = Firebase.firestore
        db.collection("tbConcert")
            .document(favorite.concertId)
            .get()
            .addOnSuccessListener { document ->
                val concertName = document.data?.get("Name").toString()
                val concertVenue = document.data?.get("Venue").toString()
                val concertDate = document.data?.get("StartConcertDate") as Timestamp
                val imageUrl = document.data!!["ImageUrl"].toString()

                val formattedStartConcertDate = sdf.format(concertDate.toDate())

                holder.concertName.text = concertName
                holder.concertVenue.text = concertVenue
                holder.concertDate.text = formattedStartConcertDate

                Picasso.get().load(imageUrl).into(holder.poster)
                holder._btnUnlike.setOnClickListener {
                    val sp = holder.itemView.context.getSharedPreferences("user", MODE_PRIVATE)
                    val user = sp.getString("user", null)
                    if (user != null) {
                        db.collection("tbUser")
                            .document(user)
                            .collection("tbFavorite")
                            .document(favorite.concertId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Concert berhasil terhapus dari daftar favorit", Toast.LENGTH_SHORT).show()
                                favoriteList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, favoriteList.size)
                                notifyDataSetChanged()
                            }
                            .addOnFailureListener { exception ->
                                Log.d("error", exception.toString())
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                holder.concertName.text = "Error loading concert"
                holder.concertDate.text = ""
            }
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }
}
