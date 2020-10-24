package com.example.equalizer.AsyncTask
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log

class DataFetcherAsyncTask(
    private var mcontext: Context,
    private var mDataFetcherListener: DataFetcherListener,
    private var selection: Long
) : AsyncTask<Void, Void, Uri>() {

   var path : String =""
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATA
    )

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: Void?): Uri? {

        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection)

    }


    override fun onPostExecute(result:Uri?) {
        super.onPostExecute(result)
        if (mDataFetcherListener!=null){
            if (result!=null) {
                mDataFetcherListener?.onDataFetched(result);
            }else {
                mDataFetcherListener?.onDataError();
            }
        }
    }
}