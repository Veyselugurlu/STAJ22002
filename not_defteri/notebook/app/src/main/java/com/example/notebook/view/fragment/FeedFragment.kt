package com.example.notebook.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notebook.R
import com.example.notebook.adapter.NotesAdapter
import com.example.notebook.databinding.FragmentFeedBinding
import com.example.notebook.databinding.FragmentLoginBinding
import com.example.notebook.model.Not
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding : FragmentFeedBinding? = null

    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var popup : PopupMenu
    private lateinit var db : FirebaseFirestore
    val notList : ArrayList<Not> = arrayListOf()
    private var adapter : NotesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFeedBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            floatingActionButtonTiklandi(it)
        }
        popup = PopupMenu(requireContext(),binding.floatingActionButton)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_pop_up,popup.menu)
        popup.setOnMenuItemClickListener(this)

        fireStoreVerileriAl()
        //notes adapterdeki paremetrleri duzgun bir şekilde kullandık
        adapter = NotesAdapter(notList,
            onItemDeleted = { not ->
                // Not silindiğinde yapılacak işlemler
                Toast.makeText(requireContext(), "${not.eklenecekNot} silindi.", Toast.LENGTH_SHORT).show()
            },
            onItemUpdated = { not ->
                // Not güncellendiğinde yapılacak işlemler
                Toast.makeText(requireContext(), "${not.eklenecekNot} güncellendi.", Toast.LENGTH_SHORT).show()
            }
        )

        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter = adapter
    }
    fun floatingActionButtonTiklandi(view : View){
        popup.show()
    }
    fun fireStoreVerileriAl(){
        val currentUserEmail = auth.currentUser?.email ?: return
        db.collection("Notlar")
            .whereEqualTo("email", currentUserEmail)
            .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null){
                Toast.makeText(requireContext(),"error.localizedMessage",Toast.LENGTH_LONG).show()
                println(error.localizedMessage)
                return@addSnapshotListener
            }
            else{
                if (value != null){
                    if (!value.isEmpty){
                        notList.clear()
                        val documents = value.documents
                        for (document in documents){
                            val eklenecekNot = document.get("not") as String
                            val email = document.get("email") as String
                            if (eklenecekNot != null && email != null) {
                                val post = Not(document.id, email, eklenecekNot)
                                notList.add(post)
                            }
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.yuklemeItem){
            val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        else if (item?.itemId == R.id.cikisItem){
            //çıkış işlemi
            auth.signOut()
            val action = FeedFragmentDirections.actionFeedFragmentToLoginFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true
    }

}