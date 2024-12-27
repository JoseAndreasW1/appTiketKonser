package com.example.apptiketkonser

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class fListConcert : Fragment() {
    private lateinit var ongoingConcert: RecyclerView
    private lateinit var upcomingConcert: RecyclerView

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler : Handler
    private lateinit var adapter: CarouselAdapter

    private lateinit var adapterOngoing: ConcertAdapter
    private lateinit var adapterUpcoming: ConcertAdapter

    val db = Firebase.firestore
    var listOnGoingConcert = ArrayList<Concert>()
    var listUpComingConcert = ArrayList<Concert>()

    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        //untuk viewpager
        init()


    }

    fun readData(db: FirebaseFirestore, onComplete: () -> Unit) {
        db.collection("tbConcert")
            .get()
            .addOnSuccessListener { result ->
                listOnGoingConcert.clear()
                listUpComingConcert.clear()
                for (document in result) {
                    val startPreOrderDate = document.data["StartPreOrderDate"] as Timestamp
                    val endPreOrderDate = document.data["EndPreOrderDate"] as Timestamp
                    val startConcertDate = document.data["StartConcertDate"] as Timestamp

                    val formattedStartPreOrderDate = sdf.format(startPreOrderDate.toDate())
                    val formattedEndPreOrderDate = sdf.format(endPreOrderDate.toDate())
                    val formattedStartConcertDate = sdf.format(startConcertDate.toDate())
                    val concert = Concert(
                        document.id,
                        document.data["Name"].toString(),
                        formattedStartPreOrderDate,
                        formattedEndPreOrderDate,
                        formattedStartConcertDate,
                        document.data["Description"].toString(),
                        document.data["ImageUrl"].toString(),
                        document.data["Price"].toString().toInt(),
                        document.data["NumberOfTickets"].toString().toInt(),
                        document.data["Venue"].toString()
                    )
                    if (startPreOrderDate.toDate().after(Date())) {
                        // Upcoming concert: Pre-order belum dimulai
                        listUpComingConcert.add(concert)
                    } else if (startPreOrderDate.toDate().before(Date()) && endPreOrderDate.toDate().after(Date())) {
                        // Ongoing concert: Pre-order sedang berlangsung
                        listOnGoingConcert.add(concert)
                    }
                }
                onComplete()
                Log.d("MainActivity Firebase", "Success")
            }
            .addOnFailureListener {
                Log.d("MainActivity Firebase", "Error getting documents: ", it)
            }
    }

    fun loadData(){
        val view = requireView()

        ongoingConcert = view.findViewById(R.id.rvOngoing)
        upcomingConcert = view.findViewById(R.id.rvUpcoming)

        readData(db){
            adapterOngoing = ConcertAdapter(listOnGoingConcert,ongoingConcert)
            adapterOngoing.setOnItemClickCallback(object : ConcertAdapter.OnItemClickCallback {
                override fun seeDetail(position: Int) {
                    val concert = listOnGoingConcert[position]
                    val intent = Intent(activity, DetailConcertActivity::class.java)
                    DetailConcertActivity.concert = concert
                    startActivity(intent)
                }
            })

            adapterUpcoming = ConcertAdapter(listUpComingConcert,upcomingConcert)
            adapterUpcoming.setOnItemClickCallback(object : ConcertAdapter.OnItemClickCallback {
                override fun seeDetail(position: Int) {
                    val concert = listUpComingConcert[position]
                    val intent = Intent(activity, DetailConcertActivity::class.java)
                    DetailConcertActivity.concert = concert
                    startActivity(intent)
                }
            })

            ongoingConcert.adapter = adapterOngoing
            upcomingConcert.adapter = adapterUpcoming


            ongoingConcert.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            upcomingConcert.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


            val _tvNoOngoingConcert = view.findViewById<TextView>(R.id.tvNoOngoingConcert)
            val _tvNoUpComingConcert = view.findViewById<TextView>(R.id.tvNoUpComingConcert)

            //Jika Concert empty
            if(listOnGoingConcert.isEmpty()){
                ongoingConcert.visibility = View.GONE
                _tvNoOngoingConcert.visibility = View.VISIBLE
            } else {
                ongoingConcert.visibility = View.VISIBLE
                _tvNoOngoingConcert.visibility = View.GONE
            }
            if(listUpComingConcert.isEmpty()){
                upcomingConcert.visibility = View.GONE
                _tvNoUpComingConcert.visibility = View.VISIBLE
            } else {
                upcomingConcert.visibility = View.VISIBLE
                _tvNoUpComingConcert.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable , 5000)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }


    private fun init(){
        val view = requireView()
        viewPager2 = view.findViewById(R.id.carouselViewPager)
        handler = Handler(Looper.myLooper()!!)
        val itemList = listOf(
            Pair(R.drawable.artist1, "Billie Eilish"),
            Pair(R.drawable.artist2, "Bruno Mars"),
            Pair(R.drawable.artist3, "Taylor Swift"),
            Pair(R.drawable.artist4, "Afgan"),
            Pair(R.drawable.artist5, "Tiara Andini")
        )

        adapter = CarouselAdapter(itemList, viewPager2)
        viewPager2.adapter = adapter

        viewPager2.setCurrentItem(1, false)

        viewPager2.offscreenPageLimit = 3

        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                adapter.notifyDataSetChanged()
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    val itemCount = adapter.itemCount
                    when (viewPager2.currentItem) {
                        0 -> viewPager2.setCurrentItem(itemCount - 2, false)
                        itemCount - 1 -> viewPager2.setCurrentItem(1, false)
                    }
                }
            }
        })


        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        viewPager2.setPageTransformer(transformer)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable , 5000)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_list_concert, container, false)
    }
}