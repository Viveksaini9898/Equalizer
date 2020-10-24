package com.example.equalizer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bullhead.equalizer.EqualizerFragment
import com.bullhead.equalizer.EqualizerModel
import com.bullhead.equalizer.Settings
import com.example.equalizer.AsyncTask.DataFetcherAsyncTask
import com.example.equalizer.AsyncTask.DataFetcherListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.volume_activity.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    var path :String=""
    var equalizerFragment: EqualizerFragment? = null
    private var mediaPlayer: MediaPlayer? = null
    var audioFileUri: String? = null
    var pathTracked=0
    var audioManager: AudioManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        loadEqualizerSettings()
        volumeControl()
        val manager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (manager.isMusicActive) {
            loadTrcakMusic()
        }
        else{
            Toast.makeText(this, "You must play a song first!", Toast.LENGTH_SHORT).show()
        }

        equalizericon.setOnClickListener {
            if (mediaPlayer!=null){
                eqFrame.visibility = View.VISIBLE
                eqFrame1.visibility = View.GONE
            }else {
                Toast.makeText(this, "You must play a song first!", Toast.LENGTH_SHORT).show()
            }
        }

        audioicon.setOnClickListener {
           eqFrame1.visibility= View.VISIBLE
            eqFrame.visibility =View.GONE
            volumeControl()
        }

    }

    private fun volumeControl() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?;
        val let =
            audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.let(seekbar::setMax);

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

    }

    inner class Receiver : BroadcastReceiver(),DataFetcherListener{
        override fun onReceive(context: Context?, intent: Intent) {
            val action: String? = intent.action
            val cmd: String? = intent.getStringExtra("command")
            Log.v("tag ", "$action / $cmd")
            val artist: String? = intent.getStringExtra("artist")
            val album: String? = intent.getStringExtra("album")
            val track: String? = intent.getStringExtra("track")
            val song_id = intent.getLongExtra("id", 0)
            Log.v("tag", "$artist:$album:$track:$song_id")
            if (context != null) {
                DataFetcherAsyncTask(context, this, song_id).execute()
            }
            var pendingResult=goAsync()
        }

        override fun onDataFetched(result: Uri) {
            Log.d("path: ", "$result $pathTracked")
            if(pathTracked<1) {
                Toast.makeText(
                    applicationContext,
                    "" + getRealPathFromURI(result),
                    Toast.LENGTH_SHORT
                ).show()
                path = getRealPathFromURI(result)!!
                mediaPlayer = MediaPlayer()
                mediaPlayer!!.setDataSource(path)
                mediaPlayer!!.prepare()
               //  mediaPlayer?.setVolume(0f,0f)

                try {
                    val sessionId = mediaPlayer!!.audioSessionId
                    mediaPlayer!!.isLooping = true
                    val equalizerFragment = EqualizerFragment.newBuilder()
                        .setAccentColor(Color.parseColor("#4caf50"))
                        .setAudioSessionId(sessionId)
                        .build()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.eqFrame, equalizerFragment)
                        .commit()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                pathTracked++
            }

        }

        override fun onDataError() {
            TODO("Not yet implemented")
        }

    }

    private fun loadTrcakMusic() {
        val iF = IntentFilter()
        iF.addAction("com.android.music.metachanged")
        iF.addAction("com.android.music.playstatechanged")
        iF.addAction("com.android.music.playbackcomplete")
        iF.addAction("com.android.music.queuechanged")
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged")
        iF.addAction("com.spotify.music.playbackstatechanged")
        iF.addAction("com.spotify.music.metadatachanged")
        iF.addAction("com.apple.android.music.metachanged")
        iF.addAction("com.apple.android.music.playstatechanged")
        iF.addAction("com.rdio.android.metachanged")
        iF.addAction("com.rdio.android.playstatechanged")
        registerReceiver(Receiver(), iF)
    }
    private fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor =
            contentResolver.query(contentUri!!, proj, null, null, null)
        try {

            if (cursor!!.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                audioFileUri = cursor.getString(column_index)
            }
        }catch (r: Exception){
            r.printStackTrace()
        }
        cursor.close()
        return audioFileUri
    }

    override fun onStop() {
        super.onStop()
        saveEqualizerSettings()
    }

    override fun onPause() {
        try {
            mediaPlayer!!.pause()
            mediaPlayer!!.start()
        } catch (ex: java.lang.Exception) {
            //ignore
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        try {
            Handler().postDelayed({ mediaPlayer?.start() }, 1000)
        } catch (ex: java.lang.Exception) {
            //ignore
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
        }
    }

    private fun saveEqualizerSettings() {
        if (Settings.equalizerModel != null) {
            val settings = EqualizerSettings()
            settings.bassStrength = Settings.equalizerModel.bassStrength
            settings.presetPos = Settings.equalizerModel.presetPos
            settings.reverbPreset = Settings.equalizerModel.reverbPreset
            settings.seekbarpos = Settings.equalizerModel.seekbarpos
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val gson = Gson()
            preferences.edit()
                .putString(PREF_KEY, gson.toJson(settings))
                .apply()
        }
    }
    private fun loadEqualizerSettings() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val gson = Gson()
        val settings = gson.fromJson(
            preferences.getString(PREF_KEY, "{}"),
            EqualizerSettings::class.java
        )
        val model = EqualizerModel()
        model.bassStrength = settings.bassStrength
        model.presetPos = settings.presetPos
        model.reverbPreset = settings.reverbPreset
        model.seekbarpos = settings.seekbarpos
        Settings.isEqualizerEnabled = true
        Settings.isEqualizerReloaded = true
        //Settings.isEqualizerDisable =false
        Settings.bassStrength = settings.bassStrength
        Settings.presetPos = settings.presetPos
        Settings.reverbPreset = settings.reverbPreset
        Settings.seekbarpos = settings.seekbarpos
        Settings.equalizerModel = model
    }

    val PREF_KEY = "equalizer"
}