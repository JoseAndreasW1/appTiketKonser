package com.example.apptiketkonser

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterRecViewConcert(private val listConcert: ArrayList<Concert>) : RecyclerView
.Adapter<AdapterRecViewConcert.ListViewHolder>(){
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var _concertName = itemView.findViewById<TextView>(R.id.concertName)
        var _concertDate = itemView.findViewById<TextView>(R.id.concertDate)
        var _concertImage = itemView.findViewById<ImageView>(R.id.concertImage)

        var _btnDetail = itemView.findViewById<Button>(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_concert, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listConcert.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var concert = listConcert[position]
        holder._concertName.text = concert.name
        holder._concertDate.text = concert.startConcertDate
        Picasso.get()
            .load(concert.imageUrl)
            .into(holder._concertImage)

        holder._btnDetail.setOnClickListener{

        //Function buat ke Detail Concert
//            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
//            intent.putExtra("concertId", concert.id)
//            holder.itemView.context.startActivity(intent)
        }
    }
}
