package com.example.notebook.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.notebook.R
import com.example.notebook.adapter.NotesAdapter
import com.example.notebook.databinding.FragmentUploadBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class UploadFragment : Fragment() {

    private var _binding : FragmentUploadBinding? = null
    private  val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db :FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       _binding = FragmentUploadBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.yukleButton.setOnClickListener { yukleTiklandi(it) }
    }


    fun yukleTiklandi(view:View){
        if (auth.currentUser != null){
            val postMap = hashMapOf<String , Any>()
            postMap.put("email",auth.currentUser!!.email.toString())
            postMap.put("not",binding.eklenecekNot.text.toString())
            postMap.put("date",Timestamp.now())

            db.collection("Notlar").add(postMap).addOnSuccessListener { documentReferance ->
                //veriş database'e yuklendi
                val action = UploadFragmentDirections.actionUploadFragmentToFeedFragment()
                Navigation.findNavController(view).navigate(action)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}