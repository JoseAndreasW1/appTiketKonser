package com.example.apptiketkonser

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import java.security.MessageDigest

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fLogin.newInstance] factory method to
 * create an instance of this fragment.
 */
class fLogin : Fragment() {
    val db = Firebase.firestore
    lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences("user", MODE_PRIVATE)
        val user = sp.getString("user", null)

        if (user != null) {
            db.collection("tbUser")
                .document(user)
                .get()
                .addOnSuccessListener { query ->
                    if (query != null) {
                        startActivity(Intent(activity, HomeActivity::class.java))
                    }
                }
        }

        val _etEmail = view.findViewById<EditText>(R.id.etEmail)
        val _etPassword = view.findViewById<EditText>(R.id.etPassword)

        val _btnSignUp = view.findViewById<Button>(R.id.btnSignUp)
        _btnSignUp.setOnClickListener {
            val mfSignup = fSignup()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                replace(
                    R.id.frameContainer,
                    mfSignup,
                    fSignup::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }

        val _btnSignIn = view.findViewById<Button>(R.id.btnSignIn)
        _btnSignIn.setOnClickListener {
            val email = _etEmail.text
            val password = _etPassword.text

            //Cek Null
            if(email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(activity, "Semua Data Harus Terisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Cek Email
            if(!email.toString().contains("@")) {
                Toast.makeText(activity, "Email Tidak Valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email.toString().lowercase(), password.toString())
        }
    }

    fun loginUser(email: String, password: String) {
        db.collection("tbUser")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    //Email tidak ditemukan
                    Toast.makeText(activity, "Email atau Password Salah!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (querySnapshot.documents[0].getString("password") != hashPassword(password)) {
                        //Password salah
                        Toast.makeText(activity, "Email atau Password Salah!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        //Login berhasil
                        Toast.makeText(activity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        HomeActivity.idUser = querySnapshot.documents[0].id

                        // Simpan data di shared preferences
                        sp = requireActivity().getSharedPreferences("user", MODE_PRIVATE)
                        val editor = sp.edit()
                        if (editor != null) {
                            editor.putString("user", querySnapshot.documents[0].id)
                            editor.apply()
                        }

                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
    }

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        val hashedPassword = hashBytes.joinToString("") { "%02x".format(it) }
        return hashedPassword
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_login, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fLogin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fLogin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}