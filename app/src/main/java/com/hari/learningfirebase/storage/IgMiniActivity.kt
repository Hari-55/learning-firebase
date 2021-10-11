package com.hari.learningfirebase.storage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.hari.learningfirebase.BuildConfig
import com.hari.learningfirebase.databinding.ActivityIgMiniBinding
import java.io.File
import java.util.UUID.randomUUID

class IgMiniActivity : AppCompatActivity(), OnClickImage {

    companion object {
        fun startActivity(context: Context) = Intent(context, IgMiniActivity::class.java)
    }

    private lateinit var binding: ActivityIgMiniBinding
    private val storage = Firebase.storage(BuildConfig.STORAGE_BASE)
    private val igMiniImage = mutableListOf<ImageModel>()
    private val db = Firebase.firestore
    private lateinit var adapter: ImageAdapter
    private val imagesRef: StorageReference = storage.reference.child("images")
    private var imageReport: Uri? = null
    private lateinit var pathImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIgMiniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUrlImage()
        initViews()
        binding.btnAddImage.setOnClickListener {
            getPictures()
        }
    }

    private fun initViews() {
        with(binding) {
            adapter = ImageAdapter(arrayListOf(), imagesRef, this@IgMiniActivity)
            rvImageIg.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@IgMiniActivity)
                adapter = this@IgMiniActivity.adapter
            }
        }
    }

    private fun getUrlImage() {
        binding.loading.visibility = View.VISIBLE
        db.collection("images").get()
            .addOnSuccessListener {
                igMiniImage.clear()
                for (document in it) {
                    val data = document.toObject(ImageModel::class.java)
                    igMiniImage.add(data)
                }
                if (igMiniImage.isEmpty()){
                    renderToast("Tidak ada gambar")
                    renderImage()
                }
                else renderImage()
                binding.loading.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Terjadi Keselahan gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                binding.loading.visibility = View.GONE
            }
    }

    private fun renderImage() {
        adapter.setData(igMiniImage)
        adapter.notifyDataSetChanged()
    }

    private fun getPictures() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permission, 200)
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent, null)
    }

    //minimum req appcompat:1.3.1
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            imageReport = it?.data?.data
            pathImage = getRealPathFromURI(it?.data?.data) ?: ""
            if (imageReport != null) postData()
            else renderToast("Tidak ada gambar diambil")
        } else {
            renderToast("Terjadi kesalahan mohon pilih gambar lagi")
        }
    }

    private fun getRealPathFromURI(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = uri?.let { this.contentResolver.query(it, projection, null, null, null) }
        val columnIndex: Int? = cursor
            ?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return columnIndex?.let { cursor.getString(it) }
    }

    private fun postData() {
        val id = randomUUID().toString()
        val nameImage = randomUUID().toString()
        binding.loading.visibility = View.VISIBLE
        val file = Uri.fromFile(File(pathImage))
        val riversRef = imagesRef.child(nameImage)
        val uploadTask = riversRef.putFile(file)
        uploadTask.addOnFailureListener {
            binding.loading.visibility = View.GONE
            renderToast("Gambar gagal diupload")
        }.addOnSuccessListener {
            val data = ImageModel(nameImage, id)
            db.collection("images").document(id).set(data)
                .addOnSuccessListener {
                    binding.loading.visibility = View.GONE
                    renderToast("Gambar berhasil diupload")
                }
                .addOnFailureListener {
                    binding.loading.visibility = View.GONE
                    renderToast("Lokasi gambar gagal disimpan")
                }
            getUrlImage()
        }
    }

    override fun click(image: ImageModel, imagesRef: StorageReference) {
        with(binding) {
            binding.loading.visibility = View.VISIBLE
            //delete image di storage
            imagesRef.delete().addOnSuccessListener {

                //Delete firestore
                db.collection("images").document(image.key ?: "").delete()
                    .addOnSuccessListener {
                        renderToast("Berhasil Menghapus gambar")
                        loading.visibility = View.GONE
                        getUrlImage()
                    }
                    .addOnFailureListener {
                        renderToast("Gagal Menghapus Gambar")
                        loading.visibility = View.GONE
                    }
            }.addOnFailureListener {
                renderToast("Gagal menghapus gambar")
                loading.visibility = View.GONE
            }
        }
    }

    private fun renderToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}