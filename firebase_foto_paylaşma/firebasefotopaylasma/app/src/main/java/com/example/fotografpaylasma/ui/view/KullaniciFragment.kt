package com.example.fotografpaylasma.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.fotografpaylasma.databinding.FragmentKullaniciBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore

class KullaniciFragment : Fragment() {
    private var _binding : FragmentKullaniciBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       _binding = FragmentKullaniciBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.kayItButton.setOnClickListener {kayitOl(it)  }
        binding.girisButton.setOnClickListener {girisYap(it)  }

        val guncelKullanici = auth.currentUser
        if (guncelKullanici != null){
            val action = KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
            Navigation.findNavController(view).navigate(action)
        }

    }
    fun kayitOl(view: View) {
        val kullaniciAdi = binding.kulAdi.text.toString()
        val email = binding.emailText.text.toString()
        val password = binding.parolaText.text.toString()

        if (kullaniciAdi.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = kullaniciAdi
                    }
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            // Firestore'a kullanıcı adı ve diğer bilgileri kaydedin
                            val db = Firebase.firestore
                            val userId = user.uid
                            val userData = hashMapOf(
                                "kul_adi" to kullaniciAdi,
                                "email" to email
                            )
                            db.collection("Users").document(userId).set(userData)
                                .addOnSuccessListener {
                                    val action = KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
                                    Navigation.findNavController(view).navigate(action)
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }


    fun girisYap(view:View) {
        val email = binding.emailText.text.toString()
        val password = binding.parolaText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                val action = KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
                Navigation.findNavController(view).navigate(action)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}