package com.example.apptiketkonser

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale


class fFavorite : Fragment() {
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: myFavoriteAdapter
    private val favoriteList = mutableListOf<Favorite>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_f_favorite, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFavorite)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = myFavoriteAdapter(favoriteList)
        recyclerView.adapter = adapter


        fetchFavorites()
        return view
    }
    private fun fetchFavorites() {
        val db = Firebase.firestore
        val sp = requireActivity().getSharedPreferences("user", MODE_PRIVATE)
        val user = sp.getString("user", null)
        if (user != null) {
            db.collection("tbUser")
                .document(user)
                .collection("tbFavorite")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("test", document.data.toString())

                        val favorite = Favorite(
                            user,
                            document.id
                        )
                        favoriteList.add(favorite)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("error", exception.toString())
                }
        }
    }
}