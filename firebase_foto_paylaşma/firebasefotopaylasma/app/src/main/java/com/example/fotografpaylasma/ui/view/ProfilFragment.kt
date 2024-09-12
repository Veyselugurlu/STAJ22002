package com.example.fotografpaylasma.ui.view

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fotografpaylasma.R
import com.example.fotografpaylasma.databinding.FragmentFeedBinding
import com.example.fotografpaylasma.databinding.FragmentProfilBinding
import com.example.fotografpaylasma.ui.adapter.PostAdapter
import com.example.fotografpaylasma.ui.model.Post
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfilFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null

    private lateinit var popup: PopupMenu
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var postAdapter: PostAdapter
    private var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        registerLaunchers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profilResim.setImageBitmap(secilenBitmap)

        binding.profilRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(postList, db, auth, ::silmeIslemiYap)
        binding.profilRecyclerView.adapter = postAdapter

        val kaydetButton = view.findViewById<Button>(R.id.kaydet)
        kaydetButton.setOnClickListener {
            profilEkle(view)
        }
        profilResmiGetir()

        binding.profilResim.setOnClickListener {
            gorselSec(it)
        }
        binding.profilFotoEkle.setOnClickListener {
            gorselSec(it)
        }
        binding.floatingActionButtonProfil.setOnClickListener { fabTiklandi(it) }

        binding.toolbarProfil.title = "Profil"
        binding.profilAd.text = auth.currentUser?.displayName
        fireStoreVerileriAl()
        popup = PopupMenu(requireContext(), binding.floatingActionButtonProfil)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)
    }

    private fun profilResmiGetir() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("Kullanıcılar").document(currentUser.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val profilResmiUrl = documentSnapshot.getString("profilResmiUrl")
                        val imageView: ImageView = view?.findViewById(R.id.profilResim)!!

                        if (profilResmiUrl != null) {
                            Glide.with(this)
                                .load(profilResmiUrl)
                                .placeholder(R.drawable.profil)
                                .error(R.drawable.profil)
                                .circleCrop()
                                .into(imageView)
                        } else {
                            imageView.setImageResource(R.drawable.profil)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Profil resmi getirilemedi: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                    val imageView: ImageView = view?.findViewById(R.id.profilResim)!!
                    imageView.setImageResource(R.drawable.profil)
                }
        }
    }

    private fun profilEkle(view: View) {
        val uuid = UUID.randomUUID()
        val gorselAdi = "$uuid.jpg"
        val reference = storage.reference
        val gorselReferansi = reference.child("imagesProfil").child(gorselAdi)

        if (secilenGorsel != null) {
            gorselReferansi.putFile(secilenGorsel!!)
                .addOnSuccessListener { uploadTask ->
                    gorselReferansi.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        val currentUser = auth.currentUser

                        if (currentUser != null) {
                            val postMap = hashMapOf<String, Any>()
                            postMap["profilResmiUrl"] = downloadUrl
                            postMap["email"] = currentUser.email.toString()
                            postMap["kullaniciAdi"] = currentUser.displayName ?: "Bilinmiyor"
                            postMap["date"] = Timestamp.now()

                            db.collection("Kullanıcılar").document(currentUser.uid)
                                .set(postMap)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Profil resmi kaydedildi", Toast.LENGTH_LONG).show()
                                    Navigation.findNavController(view).popBackStack()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

    fun gorselSec(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Galeriye gitmek için izin vermeniz gerekiyor", Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver") {
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }.show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Galeriye gitmek için izin vermeniz gerekiyor", Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver") {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun fireStoreVerileriAl() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        currentUserEmail?.let { email ->
            db.collection("Posts")
                .whereEqualTo("email", email)
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    context?.let { ctx ->
                        if (error != null) {
                            Toast.makeText(ctx, error.localizedMessage, Toast.LENGTH_LONG).show()
                        } else {
                            value?.let { snapshot ->
                                if (!snapshot.isEmpty) {
                                    postList.clear()
                                    val documents = snapshot.documents
                                    for (document in documents) {
                                        val kullaniciAdi = document.getString("kullaniciAdi") ?: "bilinmiyor"
                                        val yorum = document.getString("comment") ?: ""
                                        val downloadUrl = document.getString("downloadUrl") ?: ""
                                        val begeniSayisi = document.getLong("begeniSayisi")?.toInt() ?: 0
                                        val likedBy = document.get("likedBy") as List<String>? ?: listOf()
                                        val post = Post(kullaniciAdi, email, yorum, downloadUrl, begeniSayisi, likedBy, document.id)
                                        postList.add(post)
                                    }
                                    postAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
        }
    }
    // Silme işlemi uyarısı
    private fun silmeIslemiYap(post: Post) {
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
    private fun registerLaunchers() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                secilenGorsel = result.data?.data
                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, secilenGorsel!!)
                        secilenBitmap = ImageDecoder.decodeBitmap(source)
                    } else {
                        secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, secilenGorsel)
                    }
                    binding.profilResim.setImageBitmap(secilenBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("exception:", e.localizedMessage)
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(requireContext(), "İzni reddettiniz, izne ihtiyacımız var !!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fabTiklandi(view: View) {
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.cikisItem) {
            auth.signOut()
            Navigation.findNavController(requireView()).navigate(ProfilFragmentDirections.actionProfilFragmentToLoginFragment())
            true
        } else {
            false
        }
    }
}
