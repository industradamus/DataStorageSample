package com.example.datastoragesample.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.datastoragesample.R
import kotlinx.android.synthetic.main.list_item_loader.view.*

class LoadingAdapter : LoadStateAdapter<LoadingAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
       val root = LayoutInflater.from(parent.context)
           .inflate(R.layout.list_item_loader, parent, false)
        return LoadStateViewHolder(root)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
       holder.bind(loadState)
    }

    class LoadStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(loadState: LoadState) {
            itemView.progressBar.isVisible = loadState is LoadState.Loading
        }
    }
}