package com.example.fotografpaylasma.ui.view

import android.app.Activity.RESULT_OK
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
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.fotografpaylasma.R
import com.example.fotografpaylasma.databinding.FragmentYuklemeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class YuklemeFragment : Fragment() {
    private var _binding: FragmentYuklemeBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore // Firestore örneği

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Firebase Authentication ve Firebase Storage başlatma
        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore

        registerLaunchers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYuklemeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Yükle butonuna tıklanınca çağrılan fonksiyon
        binding.yukleButton.setOnClickListener {
            yukleTiklandi(it)
        }

        // ImageView tıklanınca galeriye gitmek için çağrılan fonksiyon
        binding.imageView.setOnClickListener {
            gorselSec(it)
        }
    }

    fun yukleTiklandi(view: View) {
        // Benzersiz bir dosya ismi oluşturma
        val uuid = UUID.randomUUID()
        val gorselAdi = "$uuid.jpg"
        val reference = storage.reference
        val gorselReferansi = reference.child("images")
            .child(gorselAdi)

        if (secilenGorsel != null) {
            gorselReferansi.putFile(secilenGorsel!!)
                .addOnSuccessListener { uploadTask ->
                    // Yükleme başarılı olunca dosyanın URL'sini alma
                    gorselReferansi.downloadUrl.addOnSuccessListener { uri ->
                        if (auth.currentUser != null) {
                            val downloadUrl = uri.toString()

                            //veri tabanına kayıt ekleme
                            val postMap = hashMapOf<String, Any>()
                            postMap["downloadUrl"] = downloadUrl
                            postMap["email"] = auth.currentUser!!.email.toString()
                            postMap["comment"] = binding.commentText.text.toString()
                            postMap["date"] = Timestamp.now()
                            postMap["likedBy"] = arrayListOf<String>() // Başlangıçta boş beğeni listesi
                            postMap["begeniSayisi"] = 0 // Başlangıç beğeni sayısı
                            // Kullanıcı adı ekleniyor
                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                postMap["kullaniciAdi"] = currentUser.displayName ?: "Bilinmiyor"
                            }

                            db.collection("Posts").add(postMap)
                                .addOnSuccessListener { documentReference ->
                                    // Veri database'e yüklendi
                                    Navigation.findNavController(view).popBackStack()
                                    Toast.makeText(requireContext(), "Gönderi Paylaşıldı", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Yükleme başarısız olursa hata mesajı gösterme
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }



    fun gorselSec(view: View) {
        // Android 13 ve üstü için izin kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // İzin yoksa kullanıcıya açıklama gösterme
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Galeriye gitmek için izin vermeniz gerekiyor", Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver") {
                            // İzin isteme
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }.show()
                } else {
                    // Direkt izin isteme
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                // İzin varsa galeriye gitme
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            // Android 13'ten düşük versiyonlar için izin kontrolü
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

    // Activity ve izinler için launcher'ları kaydetme
    private fun registerLaunchers() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    secilenGorsel = intentFromResult.data
                    try {
                        // Seçilen görseli bitmap olarak alma ve ImageView'e yerleştirme
                        secilenBitmap = if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(requireActivity().contentResolver, secilenGorsel!!)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, secilenGorsel)
                        }
                        binding.imageView.setImageBitmap(secilenBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("exception:", e.localizedMessage)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                // İzin verilirse galeriye gitme
                val intentGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentGallery)
            } else {
                // İzin verilmezse kullanıcıya mesaj gösterme
                Toast.makeText(requireContext(), "İzni reddettiniz, izne ihtiyacımız var !!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
