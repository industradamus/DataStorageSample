package com.example.datastoragesample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import com.example.datastoragesample.R
import com.example.datastoragesample.models.Photo
import com.example.datastoragesample.network.PixelsApi
import com.example.datastoragesample.network.RemoteProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val api: PixelsApi = RemoteProvider().getApi()
    private val activityDisposable: CompositeDisposable = CompositeDisposable()
    private val imageAdapter: ImageAdapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecycler()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityDisposable.clear()
    }

    private fun initRecycler() {
        imageRecycler.adapter = imageAdapter

        val dataSource = object : PositionalDataSource<Photo>() {
            private var page = 1

            override fun loadInitial(
                params: LoadInitialParams,
                callback: LoadInitialCallback<Photo>
            ) {
                api.getImageList(page++)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = {
                            callback.onResult(it?.photos ?: emptyList(), 0)
                        },
                        onError = {
                            Toast.makeText(
                                this@MainActivity,
                                "Error: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    .addTo(activityDisposable)
            }

            override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Photo>) {
                api.getImageList(page++)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = {
                            callback.onResult(it?.photos ?: emptyList())
                        },
                        onError = {
                            Log.d("XXX", it.message.orEmpty())
                            Toast.makeText(
                                this@MainActivity,
                                "Error: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    .addTo(activityDisposable)
            }
        }

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()

        val pagedList: PagedList<Photo> = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newCachedThreadPool())
            .setNotifyExecutor(ContextCompat.getMainExecutor(this))
            .build()
        imageAdapter.submitList(pagedList)
    }

}