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
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bullhead.equalizer.EqualizerFragment
import com.bullhead.equalizer.EqualizerModel
import com.bullhead.equalizer.Settings
import com.example.equalizer.AsyncTask.DataFetcherAsyncTask
import com.example.equalizer.AsyncTask.DataFetcherListener
import com.example.equalizer.receiver.AmazonReceiver
import com.example.equalizer.receiver.MIUIReceiver
import com.example.equalizer.receiver.SpotifyReceiver
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.footer.*
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
    var audio_index = 0
    var initial = 0
    var context:Context?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        loadEqualizerSettings()
//        loadAudioPath()
        volumeControl()
        setPause()
        nextAudio()
        preAudio()
        scroleText(this.songName)
        scroleText(this.ArtistName)
        val manager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (manager.isMusicActive) {
            loadTrcakMusic()
        }
        else{
            loadTrcakMusic()

           // Toast.makeText(this, "You must play a song first!", Toast.LENGTH_SHORT).show()
        }

        equalizericon.setOnClickListener {
            if (mediaPlayer!=null){
                eqFrame.visibility = View.VISIBLE
                vEqualizerIcon.visibility = View.VISIBLE
                eqFrame1.visibility = View.GONE
                vAudioicon.visibility = View.GONE
            }else {
                Toast.makeText(this, "You must play a song first!", Toast.LENGTH_SHORT).show()
            }
        }

        audioicon.setOnClickListener {
           eqFrame1.visibility= View.VISIBLE
            vAudioicon.visibility = View.VISIBLE
            eqFrame.visibility =View.GONE
            vEqualizerIcon.visibility = View.GONE
            volumeControl()
        }
        linearLayout1.setOnClickListener {

         /*   val pm1 = packageManager
            val intent1 = pm1.getLaunchIntentForPackage("com.google.android.youtube")
            startActivity(intent1)*/
            val i = Intent(Intent.ACTION_MAIN)
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            i.setPackage("com.google.android.youtube")
            startActivity(i)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj")
            intent.putExtra(
                Intent.EXTRA_REFERRER,
                Uri.parse("com.example.equalizer" + this.packageName)
            )
            this.startActivity(intent)

        }

        volume.setOnProgressChangedListener {
        }

        buttonMute.setOnClickListener {
            val audio = getSystemService(AUDIO_SERVICE) as AudioManager
            val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val percent = 0.0f
            val minVolume = (maxVolume * percent).toInt()
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, minVolume, 0)
            seekbar.setProgress(minVolume)


        }

        button30.setOnClickListener {
            val audio = getSystemService(AUDIO_SERVICE) as AudioManager
            val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val percent = 0.3f
            val thirtyVolume = (maxVolume * percent).toInt()
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, thirtyVolume, 0)
            seekbar.setProgress(thirtyVolume)

        }

        button60.setOnClickListener {
            val audio = getSystemService(AUDIO_SERVICE) as AudioManager
            val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val percent = 0.6f
            val sixtyVolume = (maxVolume * percent).toInt()
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, sixtyVolume, 0)
            seekbar.setProgress(sixtyVolume)

        }

        button100.setOnClickListener {
            val audio = getSystemService(AUDIO_SERVICE) as AudioManager
            val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val percent = 1f
            val hundredVolume = (maxVolume * percent).toInt()
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, hundredVolume, 0)
            seekbar.setProgress(hundredVolume)

        }

    }

    private fun volumeControl() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?;
        val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val curVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        seekbar.setMax(maxVolume)
        seekbar.setProgress(curVolume)
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

    }

    private fun scroleText(textView: TextView?) {
        if (textView != null) {
            textView.setHorizontallyScrolling(true)
            textView.setSingleLine(true)
            textView.isSelected = true
            textView.isFocusable = true
            textView.isFocusableInTouchMode = true
            textView.ellipsize = TextUtils.TruncateAt.MARQUEE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val index = seekbar.progress
            seekbar.progress = index + 1
            return true
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            val index = seekbar.progress
            seekbar.progress = index - 1
            return true
        }
        return super.onKeyDown(keyCode, event)
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
                mediaPlayer!!.currentPosition
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
        val filter = IntentFilter()
        filter.addAction("com.android.music.metachanged")
        filter.addAction("com.android.music.playstatechanged")
        filter.addAction("com.android.music.playbackcomplete")
        filter.addAction("com.android.music.queuechanged")
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(Receiver(), filter)

        val  filter1=IntentFilter("com.spotify.music.playbackstatechanged")
        filter1.addAction("com.spotify.music.metadatachanged")
        filter1.addAction("com.spotify.music.queuechanged")
        registerReceiver(SpotifyReceiver(),filter1)

        val  filter2=IntentFilter("com.amazon.mp3.playstatechanged")
        filter2.addAction("com.amazon.mp3.metachanged")
        filter2.addAction("com.amazon.mp3.playbackcomplete")
        registerReceiver(AmazonReceiver(),filter2)

        val  filter3=IntentFilter(".player.metachanged")
        filter3.addAction("com.miui.player.playstatechanged")
        filter3.addAction("com.miui.player.playbackcomplete")
        registerReceiver(MIUIReceiver(),filter3)
    }
    private fun getRealPathFromURI(contentUri: Uri?): String? {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor =
            contentResolver.query(contentUri!!, null, null, null, null)
        try {

            if (cursor!!.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                audioFileUri = cursor.getString(column_index)
                songName.setText(title)
                ArtistName.setText(artist)
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
       // saveAudioPath()
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
        mediaPlayer?.start()
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

   /* private fun saveAudioPath() {
        if (mediaPlayer != null) {
            val audioPath = AudioPath()
            audioPath.path= path

            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            val gson1 = Gson()
            pref.edit()
                .putString(PREF_KEY1, gson1.toJson(audioPath))
                .apply()
        }
    }

    private fun loadAudioPath() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gson1 = Gson()
        val settings = gson1.fromJson(
            pref.getString(PREF_KEY1, "{}"),
            AudioPath::class.java
        )
        var model = path
         model=settings.path
    }
*/
    val PREF_KEY = "equalizer"
  //  val PREF_KEY1 ="audiopath"

    /*private fun playAudio(pos: Int) {
        try {
            mediaPlayer!!.reset()
            if (path != null && path> 0.toString()) {
                val audio: Uri = Uri.parse(path)
                if (audio != null) {
                    mediaPlayer!!.setDataSource(this, audio)
                }
            }
            if (initial == 0) {
                mediaPlayer!!.prepare()
                pause.setImageResource(R.drawable.play_arrow_green_500_24dp)
                audio_index = pos
                initial = 1
            } else {
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
                Toast.makeText(applicationContext, "Play music", Toast.LENGTH_LONG).show()
                pause.setImageResource(R.drawable.pause_green_500_24dp)
             //   audio_name.setText(audioArrayList.get(pos).getAudioTitle())
                audio_index = pos
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    */
    private fun setPause() {
        pause.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                pause.setImageResource(R.drawable.btn_play)
                val i = Intent("com.android.music.musicservicecommand")
                i.putExtra("command", "pause")
                sendBroadcast(i)
            } else {
                mediaPlayer!!.start()
                pause.setImageResource(R.drawable.btn_pause)
                val i = Intent("com.android.music.musicservicecommand")
                i.putExtra("command", "play")
                sendBroadcast(i)

            }

        }
    }

    private fun nextAudio() {
        next.setOnClickListener {
            val i = Intent("com.android.music.musicservicecommand")
            i.putExtra("command", "next")
            sendBroadcast(i)


        }
    }

    private fun preAudio() {
        prev.setOnClickListener {
            val i = Intent("com.android.music.musicservicecommand")
            i.putExtra("command", "previous")
            sendBroadcast(i)
        }
    }
}