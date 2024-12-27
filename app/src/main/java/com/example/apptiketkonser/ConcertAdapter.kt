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
class ConcertAdapter(private val items: List<Concert>, // List Concerts
                     private val recyclerView: RecyclerView
) : RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback


    //function yang akan dioveride
    interface OnItemClickCallback {
        fun seeDetail(position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ConcertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.concertImage)
        val textOverlay: LinearLayout = itemView.findViewById(R.id.textOverlay)
        val concertDate: TextView = itemView.findViewById(R.id.concertDate)
        val btnDetail = itemView.findViewById<Button>(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_concert, parent, false)
        return ConcertViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConcertViewHolder, position: Int) {
        val concert = items[position]
        val sdfInput = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH) //format date dari DB Firebase
        val sdfOutput = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH) //format date
        val parsedDate = sdfInput.parse(concert.startPreOrderDate)
        holder.concertDate.text = sdfOutput.format(parsedDate!!)

        Picasso.get()
            .load(concert.imageUrl)
            .into(holder.imageView)

        //default detail gone
        holder.textOverlay.visibility = View.GONE

        holder.itemView.setOnClickListener { //tutup buka detail
            if (holder.textOverlay.visibility == View.GONE) {
                holder.textOverlay.visibility = View.VISIBLE
                holder.concertDate.visibility = View.VISIBLE
            } else {
                holder.textOverlay.visibility = View.GONE
                holder.concertDate.visibility = View.GONE
            }
        }

        holder.btnDetail.setOnClickListener {
            onItemClickCallback.seeDetail(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
