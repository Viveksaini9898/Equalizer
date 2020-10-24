package com.example.equalizer.AsyncTask

import android.net.Uri


interface DataFetcherListener {

    fun onDataFetched(uri: Uri)
    fun onDataError()

}