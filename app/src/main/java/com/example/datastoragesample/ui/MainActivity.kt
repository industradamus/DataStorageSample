package com.example.datastoragesample.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava2.flowable
import com.example.datastoragesample.R
import com.example.datastoragesample.network.PixelsApi
import com.example.datastoragesample.network.RemoteProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val api: PixelsApi = RemoteProvider().getApi()
    private val dataSource: PhotoPaginationDataSource = PhotoPaginationDataSource(api)
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
        imageRecycler.adapter = imageAdapter.withLoadStateFooter(LoadingAdapter())

        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                maxSize = PagingConfig.MAX_SIZE_UNBOUNDED,
                prefetchDistance = PREFETCH_DISTANCE,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { dataSource }
        )
            .flowable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { pagingData -> imageAdapter.submitData(lifecycle, pagingData) },
                onComplete = {
                    Toast.makeText(this, "OnComplete", Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(this, "OnError with error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
            .addTo(activityDisposable)
    }

    companion object {

        private const val PAGE_SIZE = 15
        private const val PREFETCH_DISTANCE = 1
    }
}