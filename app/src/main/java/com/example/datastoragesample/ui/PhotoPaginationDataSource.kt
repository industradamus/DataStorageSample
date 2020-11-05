package com.example.datastoragesample.ui

import androidx.paging.rxjava2.RxPagingSource
import com.example.datastoragesample.models.Photo
import com.example.datastoragesample.models.PixelsResponse
import com.example.datastoragesample.network.PixelsApi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class PhotoPaginationDataSource(
    private val api: PixelsApi
) : RxPagingSource<Int, Photo>() {
    

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Photo>> {
        val position = params.key ?: 1

        return api.getImageList(position)
            .map { response -> toLoadResult(response, position) }
            .onErrorReturn { error -> LoadResult.Error(error) }
            .subscribeOn(Schedulers.io())
    }

    private fun toLoadResult(
        response: PixelsResponse,
        position: Int
    ): LoadResult<Int, Photo> {
        val photos = response.photos
        return LoadResult.Page(
            data = photos,
            prevKey = if (position == 1) null else position - 1,
            nextKey = position + 1
        )
    }
}