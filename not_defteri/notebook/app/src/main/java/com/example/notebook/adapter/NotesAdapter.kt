package com.example.notebook.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notebook.R
import com.example.notebook.databinding.CardLayoutBinding
import com.example.notebook.model.Not
import com.google.firebase.firestore.FirebaseFirestore

class NotesAdapter(
    private val notList: ArrayList<Not>,
    private val onItemUpdated: (Not) -> Unit,
    private val onItemDeleted: (Not) -> Unit):
    RecyclerView.Adapter<NotesAdapter.NotHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class NotHolder(val binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotHolder {
        val binding = CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotHolder(binding)
    }

    override fun getItemCount(): Int {
        return notList.size
    }

    override fun onBindViewHolder(holder: NotHolder, position: Int) {
        val currentNot = notList[position]
        holder.binding.kullaniciNot.text = currentNot.eklenecekNot

        holder.binding.notSil.setOnClickListener {
            val documentId = currentNot.id // Belge ID'sini tutalım
            db.collection("Notlar").document(documentId).delete().addOnSuccessListener {
                // Başarıyla silindiğinde RecyclerView'dan kaldır
                val removedPosition = notList.indexOfFirst { it.id == documentId }
                if (removedPosition != -1) {
                    notList.removeAt(removedPosition)
                    notifyItemRemoved(removedPosition)
                    notifyItemRangeChanged(removedPosition, notList.size)
                    onItemDeleted(currentNot)
                }
            }.addOnFailureListener {
                Toast.makeText(holder.itemView.context,"silme başarısız${it.message}",Toast.LENGTH_LONG).show()
            }
        }
        holder.binding.editNot.setOnClickListener {
            showUpdateDialog(holder.itemView.context, currentNot)
        }
    }
    private fun showUpdateDialog(context: Context, not: Not) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_not, null)
        val editText = dialogView.findViewById<EditText>(R.id.editTextNot)
        editText.setText(not.eklenecekNot)

        AlertDialog.Builder(context)
            .setTitle("Not Güncelle")
            .setView(dialogView)
            .setPositiveButton("Güncelle") { _, _ ->
                val updatedText = editText.text.toString()
                val updatedNot = not.copy(eklenecekNot = updatedText)

                // Firestore güncellemesi
                db.collection("Notlar").document(not.id).update("not", updatedText)
                    .addOnSuccessListener {
                        val position = notList.indexOfFirst { it.id == not.id }
                        if (position != -1) {
                            notList[position] = updatedNot
                            notifyItemChanged(position)
                            onItemUpdated(updatedNot)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Güncelleme başarısız: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .setNegativeButton("İptal", null)
            .show()
    }
}
