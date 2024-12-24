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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

class MyTicketAdapter(
    private val transactionList: List<Transaction>

) : RecyclerView.Adapter<MyTicketAdapter.MyTicketViewHolder>() {
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)

    class MyTicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.poster)
//        val QRPoster: ImageView = itemView.findViewById(R.id.qr)
        val concertName: TextView = itemView.findViewById(R.id.concertName)
        val concertDate: TextView = itemView.findViewById(R.id.concertDate)
        val transactionDate: TextView = itemView.findViewById(R.id.purchaseDate)
        val concertVenue :TextView = itemView.findViewById(R.id.concertVenue)

        val _btnDetail = itemView.findViewById<ImageView>(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return MyTicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyTicketViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.transactionDate.text = transaction.transactionDate

        // Query tbconcert table to get concert details
        val db = Firebase.firestore
        db.collection("tbConcert")
            .document(transaction.concertId)
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

                    holder._btnDetail.setOnClickListener {
                        val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.qr_dialog, null)
                        val qrImageView = dialogView.findViewById<ImageView>(R.id.qrImageView)

                        //Menggambar QR Code
                        val qrCodeString = concertName
                        val writer = QRCodeWriter()
                        val bitMatrix = writer.encode(qrCodeString, BarcodeFormat.QR_CODE, 512, 512)
                        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
                        for ( x in 0 until bitmap.width){
                            for (y in 0 until bitmap.height){
                                bitmap.setPixel(x, y, if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                            }
                        }
                        qrImageView.setImageBitmap(bitmap)

                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("QR Code for "+concertName)
                            .setView(dialogView) // Atur tampilan dialog
                            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
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
