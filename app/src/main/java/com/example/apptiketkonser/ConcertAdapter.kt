package com.example.apptiketkonser

import android.view.LayoutInflater
import android.view.MotionEvent
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
                     private val recyclerView: RecyclerView
) : RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun seeDetail(position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ConcertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.concertImage)
        val textOverlay: LinearLayout = itemView.findViewById(R.id.textOverlay)
        val concertName: TextView = itemView.findViewById(R.id.concertName)
        val concertDate: TextView = itemView.findViewById(R.id.concertDate)
        val btnDetail = itemView.findViewById<Button>(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_concert, parent, false) // Make sure this layout matches your design
        return ConcertViewHolder(view)
    }

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

        // Set initial visibility to GONE for textOverlay and concertName
        holder.textOverlay.visibility = View.GONE
        holder.concertName.visibility = View.GONE

        // Toggle visibility of text when item is clicked
        holder.itemView.setOnClickListener {
            // Toggle visibility
            if (holder.textOverlay.visibility == View.GONE) {
                holder.textOverlay.visibility = View.VISIBLE
                holder.concertName.visibility = View.VISIBLE
            } else {
                holder.textOverlay.visibility = View.GONE
                holder.concertName.visibility = View.GONE
            }
        }

        // Set detail button click listener
        holder.btnDetail.setOnClickListener {
            onItemClickCallback.seeDetail(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
