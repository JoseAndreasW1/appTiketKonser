package com.example.apptiketkonser

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

class MyTicketAdapter(
    private val transactionList: List<Transaction>

) : RecyclerView.Adapter<MyTicketAdapter.MyTicketViewHolder>() {
    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.ENGLISH)

    class MyTicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.poster)
//        val QRPoster: ImageView = itemView.findViewById(R.id.qr)
        val concertName: TextView = itemView.findViewById(R.id.concertName)
        val concertDate: TextView = itemView.findViewById(R.id.concertDate)
        val transactionDate: TextView = itemView.findViewById(R.id.purchaseDate)
        val concertVenue :TextView = itemView.findViewById(R.id.concertVenue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return MyTicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyTicketViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.transactionDate.text = transaction.transactionDate // Example: format date if needed

        // Query tbconcert table to get concert details
        val db = Firebase.firestore
        db.collection("tbConcert")
            .document(transaction.concertId.toString())
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
//                    Picasso.get().load(imageUrl).into(holder.QRPoster)
            }
            .addOnFailureListener { e ->
                // Handle errors
                holder.concertName.text = "Error loading concert"
                holder.concertDate.text = ""
            }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}
