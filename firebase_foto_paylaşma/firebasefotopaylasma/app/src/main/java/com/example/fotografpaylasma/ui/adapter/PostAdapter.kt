package com.example.fotografpaylasma.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fotografpaylasma.R
import com.example.fotografpaylasma.databinding.RecyclerRowBinding
import com.example.fotografpaylasma.ui.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PostAdapter(
    private val postList : ArrayList<Post>,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val silmeIslemi: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostHolder>(){

    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val currentPost = postList[position]

        holder.binding.kulaniciAdi.text = currentPost.kullaniciAdi
        holder.binding.begeniSayisi.text = currentPost.begeniSayisi.toString()
        holder.binding.recyclerCommentText.text = currentPost.comment
        Picasso.get().load(currentPost.downloadUrl).into(holder.binding.recyclerImageView)

        // Silme butonunun görünürlüğünü ayarla
        val currentUserEmail = auth.currentUser?.email
        if (currentPost.email == currentUserEmail) {
            holder.binding.silButton.visibility = View.VISIBLE
        } else {
            holder.binding.silButton.visibility = View.GONE
        }

        // Silme butonuna tıklama
        holder.binding.silButton.setOnClickListener {
            silmeIslemi(currentPost)
        }
        // Kullanıcının beğenip beğenmediğini kontrol et
        val isLiked = currentUserEmail?.let { currentPost.likedBy.contains(it) } ?: false

        holder.binding.likeButton.setImageResource(if (isLiked) R.drawable.kalp_dolu else R.drawable.kalp)

        // Beğeni butonuna tıklanınca yapılacaklar
        holder.binding.likeButton.setOnClickListener {
            if (currentUserEmail != null) {
                if (!isLiked) {
                    // Beğenme işlemi
                    val newLikeCount = currentPost.begeniSayisi + 1
                    val updatedLikedBy = currentPost.likedBy + currentUserEmail

                    // Firestore'da beğeni sayısını ve likedBy listesini güncelle
                    val postDocumentRef = db.collection("Posts").document(currentPost.documentId) // DocumentId'yi kullanarak erişim sağlıyoruz
                    postDocumentRef.update("begeniSayisi", newLikeCount, "likedBy", updatedLikedBy)
                        .addOnSuccessListener {
                            // Local olarak da güncelle
                            currentPost.begeniSayisi = newLikeCount
                            currentPost.likedBy = updatedLikedBy
                            notifyItemChanged(position)  // UI'yi güncelle
                        }
                        .addOnFailureListener { exception ->
                            // Hata mesajını logla
                            Log.e("Firestore Error", "Beğeni kaydedilemedi: ${exception.localizedMessage}")
                            Toast.makeText(holder.itemView.context, "Beğeni kaydedilemedi.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Eğer kullanıcı zaten beğenmişse
                    Toast.makeText(holder.itemView.context, "Bu gönderiyi zaten beğendiniz.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

