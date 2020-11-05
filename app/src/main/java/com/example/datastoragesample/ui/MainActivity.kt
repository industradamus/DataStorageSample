package com.example.datastoragesample.ui

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val api: PixelsApi = RemoteProvider().getApi()
    private val dataSource: PhotoPaginationDataSource = PhotoPaginationDataSource(api)
    private val activityDisposable: CompositeDisposable = CompositeDisposable()
    private val imageAdapter: ImageAdapter = ImageAdapter(::onPhotoClick)

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

    // deprecated
    private fun deprecatedSharing(photo: Bitmap) {
        // deprecated
        val bitmapPath = MediaStore.Images.Media.insertImage(contentResolver, photo, "some title", null)

        val bitmapUri = Uri.parse(bitmapPath)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    private fun onPhotoClick(photo: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            compressPhoto(uri, photo)
            sharePhoto(uri)
        } catch (e: IOException) {
            if (uri != null) {
                contentResolver.delete(uri, null, null)
            }
            throw IOException(e)
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        }
    }

    private fun compressPhoto(uri: Uri?, photo: Bitmap) =
        uri?.let {
            val stream = contentResolver.openOutputStream(uri)

            if (!photo.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                throw IOException("Failed to save bitmap.")
            }

        } ?: throw IOException("Failed to create new MediaStore record")

    private fun sharePhoto(uri: Uri?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    companion object {

        private const val PAGE_SIZE = 15
        private const val PREFETCH_DISTANCE = 1
    }
}