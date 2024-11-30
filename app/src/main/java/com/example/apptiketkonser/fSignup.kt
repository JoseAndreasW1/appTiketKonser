package com.example.apptiketkonser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.security.MessageDigest

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fSignup.newInstance] factory method to
 * create an instance of this fragment.
 */
class fSignup : Fragment() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val _etEmail = view.findViewById<EditText>(R.id.etEmail)
        val _etName = view.findViewById<EditText>(R.id.etName)
        val _etPassword = view.findViewById<EditText>(R.id.etPassword)
        val _etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        val _btnSignUp = view.findViewById<Button>(R.id.button)

        val email = _etEmail.text
        val name = _etName.text
        val password = _etPassword.text
        val confirmPassword = _etConfirmPassword.text

        _btnSignUp.setOnClickListener {
            //Cek Null
            if(email.isNullOrEmpty() || name.isNullOrEmpty() || password.isNullOrEmpty() || confirmPassword.isNullOrEmpty()){
                Toast.makeText(activity, "Semua Data Harus Terisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Cek Email
            if(!email.toString().contains("@")){
                Toast.makeText(activity, "Email Tidak Valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Cek Password
            if(password.toString() != confirmPassword.toString()){
                Toast.makeText(activity, "Password Tidak Sesuai!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email.toString().lowercase(), password.toString(), name.toString())
        }
    }

    fun registerUser(email: String, password: String, name: String){
        db.collection("tbUser")
            //Cek email unik atau tidak
            .whereEqualTo("Email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    //Email sudah digunakan
                    Toast.makeText(activity, "Email Sudah Digunakan!", Toast.LENGTH_SHORT).show()
                } else {
                    //Hash password
                    val hashedPassword = hashPassword(password)

                    val user = User(email, name, hashedPassword, 500000)

                    db.collection("tbUser")
                        .add(user)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Register Berhasil!", Toast.LENGTH_SHORT).show()

                            //Pindah ke fragment Login
                            val mfLogin = fLogin()
                            val mFragmentManager = parentFragmentManager
                            mFragmentManager.beginTransaction().apply {
                                replace(
                                    R.id.frameContainer,
                                    mfLogin,
                                    fLogin::class.java.simpleName
                                )
                                commit()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "Gagal Register!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Gagal memeriksa email!", Toast.LENGTH_SHORT).show()
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
        return inflater.inflate(R.layout.fragment_f_signup, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fSignup.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fSignup().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}