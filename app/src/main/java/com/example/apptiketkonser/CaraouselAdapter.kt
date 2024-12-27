package com.example.apptiketkonser

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class CarouselAdapter(
    private val items: List<Pair<Int, String>>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<CarouselAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textOverlay: TextView = itemView.findViewById(R.id.textOverlay)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_caraousel, parent, false)
        return ImageViewHolder(view)
    }
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        //untuk infinite scroll
        val realPosition = when (position) {
            0 -> items.size - 1
            itemCount - 1 -> 0
            else -> position - 1
        }
        val item = items[realPosition]

        //initialize isi
        holder.imageView.setImageResource(item.first)
        holder.textOverlay.text = item.second

        val currentItem = viewPager2.currentItem
        if (realPosition == currentItem-1) {
            holder.textOverlay.visibility = View.VISIBLE
        } else {
            holder.textOverlay.visibility = View.GONE
        }
    }

    // +2, awal ada item paling belakang, akhir ada item paling depan
    override fun getItemCount(): Int {
        return items.size + 2
    }
}
