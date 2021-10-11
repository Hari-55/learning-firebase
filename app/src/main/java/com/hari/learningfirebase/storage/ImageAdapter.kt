package com.hari.learningfirebase.storage

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import com.hari.learningfirebase.R
import com.hari.learningfirebase.databinding.ItemImageIgBinding

class ImageAdapter(
    private val image: ArrayList<ImageModel>,
    private val imagesRef: StorageReference,
    private val listener: OnClickImage
) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    fun setData(image: List<ImageModel>) {
        this.image.clear()
        this.image.addAll(image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageIgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.renderImage(image[position])
    }

    override fun getItemCount() = image.size

    inner class ViewHolder(private val binding: ItemImageIgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun renderImage(image: ImageModel) {
            val path: StorageReference? = image.name?.let { imagesRef.child(it) }

            path?.downloadUrl
                ?.addOnSuccessListener {
                    //Dokumentasi Glide
                    //https://bumptech.github.io/glide/
                    Glide
                        .with(itemView.context)
                        .load(it.toString())
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.bg_loading)
                        .fallback(R.color.black)
                        .error(R.color.black)
                        .into(binding.ivPreview)
                }
                ?.addOnFailureListener {
                    it.message?.let { error -> Log.i("ImageAdapter", error) }
                }
            itemView.setOnClickListener {
                path?.let { path -> listener.click(image, path) }
            }
        }
    }
}