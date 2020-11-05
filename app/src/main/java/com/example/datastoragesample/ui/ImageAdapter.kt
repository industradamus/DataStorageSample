package com.example.datastoragesample.ui

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.datastoragesample.R
import com.example.datastoragesample.models.Photo
import kotlinx.android.synthetic.main.list_item_image.view.*

class ImageAdapter(private val onPhotoClickListener: (Bitmap) -> Unit) : PagingDataAdapter<Photo, ImageAdapter.ViewHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onPhotoClickListener) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal fun bind(photo: Photo, onPhotoClickListener: (Bitmap) -> Unit) {
            with(itemView) {
                setOnClickListener { onPhotoClickListener(imageContainer.drawToBitmap()) }

                Glide.with(imageContainer)
                    .load(photo.src.medium)
                    .into(imageContainer)
            }
        }
    }

    class DiffUtils : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
            oldItem == newItem
    }
}