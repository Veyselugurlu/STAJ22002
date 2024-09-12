package com.example.fotografpaylasma.ui.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fotografpaylasma.R
import com.example.fotografpaylasma.databinding.FragmentFeedBinding
import com.example.fotografpaylasma.ui.adapter.PostAdapter
import com.example.fotografpaylasma.ui.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedFragment : Fragment() {
    private var _binding : FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth :FirebaseAuth
    private lateinit var db : FirebaseFirestore
    val postList :  ArrayList<Post> = arrayListOf()
    private var adapter : PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title="Ana Sayfa"
        fireStoreVerileriAl()

        // Adapter ayarlanıyor. Verileri RecyclerView'a göstermek için.
        adapter = PostAdapter(postList,db,auth, ::silmeIslemiYap)   //// silme işlemi fonksiyonu gönderiliyor
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter = adapter
    }

    private fun fireStoreVerileriAl() {
        // Firebase Firestore'dan "Posts" koleksiyonundaki veriler alınır ve tarihe göre sıralanır.

        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            context?.let { ctx ->       // Fragment'e bağlı context
                if (error != null) {
                    Toast.makeText(ctx, error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    value?.let { snapshot ->
                        if (!snapshot.isEmpty) {
                            postList.clear()
                            val documents = snapshot.documents
                            for (document in documents) {
                                val kullaniciAdi = document.getString("kullaniciAdi") ?: "bilinmiyor"
                                val comment = document.getString("comment") ?: ""
                                val email = document.getString("email") ?: ""
                                val downloadUrl = document.getString("downloadUrl") ?: ""
                                val begeniSayisi = document.getLong("begeniSayisi")?.toInt() ?: 0
                                val likedBy = document.get("likedBy") as? List<String> ?: listOf()

                                val post = Post(kullaniciAdi, email, comment, downloadUrl, begeniSayisi, likedBy,document.id)
                                postList.add(post)
                            }
                            adapter?.notifyDataSetChanged()    // Adapter'i güncelle
                        }
                    }
                }
            }
        }
    }
    // Silme işlemi yapılacağı zaman bu metod çağrılır.
    private fun silmeIslemiYap(post: Post) {
        // Eğer post'u paylaşan kişi mevcut kullanıcıysa (post.email)
        if (post.email == auth.currentUser?.email) {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Gönderiyi Sil")
            alertDialog.setMessage("Bu gönderiyi silmek istediğinizden emin misiniz?")
            alertDialog.setPositiveButton("Evet") { dialog, which ->
                // Firestore'dan silme işlemi
                db.collection("Posts").document(post.documentId).delete().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Gönderi silindi", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Silme hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
            alertDialog.setNegativeButton("Hayır") { dialog, which ->
                // İşlem iptal edildi
                dialog.dismiss()
            }
            alertDialog.show()
        } else {
            Toast.makeText(requireContext(), "Sadece kendi gönderinizi silebilirsiniz", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}