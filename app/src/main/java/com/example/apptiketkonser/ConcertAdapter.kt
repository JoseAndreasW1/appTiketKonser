package com.example.apptiketkonser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class ConcertAdapter(private val items: List<Concert>, // List of Concerts
                            private val viewPager2: ViewPager2
) : RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder>() {

    private lateinit var onItemClickCallback : OnItemClickCallback

    interface OnItemClickCallback {
        fun seeDetail(position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ConcertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.concertImage)
        val textOverlay : LinearLayout = itemView.findViewById(R.id.textOverlay)
        val concertName: TextView = itemView.findViewById(R.id.concertName)
        val concertDate: TextView = itemView.findViewById(R.id.concertDate)
        val btnDetail = itemView.findViewById<Button>(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_concert, parent, false) // Make sure this layout matches your design
        return ConcertViewHolder(view)
    }

//    override fun onBindViewHolder(holder: ConcertViewHolder, position: Int) {
//        val realPosition = when (position) {
//            0 -> items.size - 1
//            itemCount - 1 -> 0
//            else -> position - 1
//        }
//        val concert = items[realPosition]
//        holder.concertName.text = concert.name
//        holder.concertDate.text = concert.startPreOrderDate
//        Picasso.get()
//            .load(concert.imageUrl)
//            .into(holder.imageView)
//
//        val currentItem = viewPager2.currentItem
//        if (realPosition == currentItem-1) {
//            holder.textOverlay.visibility = View.VISIBLE
//            holder.concertName.visibility = View.VISIBLE
//
//        } else {
//            holder.textOverlay.visibility = View.GONE
//            holder.concertName.visibility = View.GONE
//
//        }
//    }

    override fun onBindViewHolder(holder: ConcertViewHolder, position: Int) {
        val concert = items[position] // Directly bind the position without adjustments
        holder.concertName.text = concert.name
        val sdfInput = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH)
        val sdfOutput = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val parsedDate = sdfInput.parse(concert.startPreOrderDate)
        holder.concertDate.text = sdfOutput.format(parsedDate!!)

        Picasso.get()
            .load(concert.imageUrl)
            .into(holder.imageView)

        // Optional: If you still want to highlight the current item
        val currentItem = viewPager2.currentItem
        if (position == currentItem) {
            holder.textOverlay.visibility = View.VISIBLE
            holder.concertName.visibility = View.VISIBLE
        } else {
            holder.textOverlay.visibility = View.GONE
            holder.concertName.visibility = View.GONE
        }

        holder.btnDetail.setOnClickListener {
            onItemClickCallback.seeDetail(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size //+2 buat infinite loop
    }
}

