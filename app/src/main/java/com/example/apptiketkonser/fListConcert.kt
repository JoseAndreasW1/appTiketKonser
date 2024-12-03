package com.example.apptiketkonser

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
    private lateinit var ongoingConcertViewPager: ViewPager2
    private lateinit var upcomingConcertViewPager: ViewPager2

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler : Handler
    private lateinit var adapter: CarouselAdapter
    private lateinit var adapterOngoing: ConcertAdapter
    private lateinit var adapterUpcoming: ConcertAdapter

    val db = Firebase.firestore
    var listOnGoingConcert = ArrayList<Concert>()
    var listUpComingConcert = ArrayList<Concert>()
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        //view pager, artist
        init()
        setUpTransformer()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable , 5000)
            }
        })
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
                        1,
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
                    } else if (startPreOrderDate.toDate().before(Date()) && endPreOrderDate.toDate().after(
                            Date()
                        )) {
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
        ongoingConcertViewPager = view.findViewById(R.id.viewPagerOnGoingConcert)
        upcomingConcertViewPager = view.findViewById(R.id.viewPagerUpComingConcert)
        readData(db){

            adapterOngoing = ConcertAdapter(listOnGoingConcert,ongoingConcertViewPager)
            adapterUpcoming = ConcertAdapter(listUpComingConcert,upcomingConcertViewPager)

            ongoingConcertViewPager.adapter = adapterOngoing
            upcomingConcertViewPager.adapter = adapterUpcoming

            ongoingConcertViewPager.setCurrentItem(0, false)
            upcomingConcertViewPager.setCurrentItem(0, false)

            ongoingConcertViewPager.offscreenPageLimit = 3
            upcomingConcertViewPager.offscreenPageLimit = 3

            ongoingConcertViewPager.clipToPadding = false
            upcomingConcertViewPager.clipToPadding = false

            ongoingConcertViewPager.clipChildren = false
            upcomingConcertViewPager.clipChildren = false

            ongoingConcertViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            upcomingConcertViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val _tvNoOngoingConcert = view.findViewById<TextView>(R.id.tvNoOngoingConcert)
            val _tvNoUpComingConcert = view.findViewById<TextView>(R.id.tvNoUpComingConcert)

            ongoingConcertViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    adapterOngoing.notifyDataSetChanged()
//                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
//                        val itemCount = adapter.itemCount
//                        when (viewPager2.currentItem) {
//                            0 -> viewPager2.setCurrentItem(itemCount - 2, false) // Jump to the last real item
//                            itemCount - 1 -> viewPager2.setCurrentItem(1, false) // Jump to the first real item
//                        }
//                    }
                }
            })
            upcomingConcertViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    adapterUpcoming.notifyDataSetChanged()
//                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
//                        val itemCount = adapter.itemCount
//                        when (viewPager2.currentItem) {
//                            0 -> viewPager2.setCurrentItem(itemCount - 2, false) // Jump to the last real item
//                            itemCount - 1 -> viewPager2.setCurrentItem(1, false) // Jump to the first real item
//                        }
//                    }
                }
            })


            //Jika Concert empty
            if(listOnGoingConcert.isEmpty()){
                ongoingConcertViewPager.visibility = View.GONE
                _tvNoOngoingConcert.visibility = View.VISIBLE
            } else {
                ongoingConcertViewPager.visibility = View.VISIBLE
                _tvNoOngoingConcert.visibility = View.GONE
            }
            if(listUpComingConcert.isEmpty()){
                upcomingConcertViewPager.visibility = View.GONE
                _tvNoUpComingConcert.visibility = View.VISIBLE
            } else {
                upcomingConcertViewPager.visibility = View.VISIBLE
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

    private fun setUpTransformer(){
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
//        transformer.addTransformer { page, position ->
//            val scale = 0.85f + (1 - abs(position)) * 0.15f
//            page.scaleY = scale
//            page.scaleX = scale
//        }
        viewPager2.setPageTransformer(transformer)
        ongoingConcertViewPager.setPageTransformer(transformer)
        upcomingConcertViewPager.setPageTransformer(transformer)

    }

    // Apply the transformer to all ViewPagers

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
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_list_concert, container, false)
    }
}