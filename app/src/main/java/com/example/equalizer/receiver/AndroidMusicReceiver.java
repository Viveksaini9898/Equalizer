package com.example.equalizer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AndroidMusicReceiver extends BroadcastReceiver {
    static final class BroadcastTypes {
        static final String ANDROID_PACKAGE = "com.android.music";
        static final String PLAYBACK_STATE_CHANGED =  ANDROID_PACKAGE + ".playbackstatechanged";
        static final String QUEUE_CHANGED = ANDROID_PACKAGE + ".queuechanged";
        static final String METADATA_CHANGED = ANDROID_PACKAGE + ".metadatachanged";
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Android",Toast.LENGTH_LONG).show();
        long timeSentInMs = intent.getLongExtra("timeSent", 0L);

        String action = intent.getAction();

        if (action.equals(AndroidMusicReceiver.BroadcastTypes.METADATA_CHANGED)) {
            String trackId = intent.getStringExtra("id");
            String artistName = intent.getStringExtra("artist");
            String albumName = intent.getStringExtra("album");
            String trackName = intent.getStringExtra("track");
            int trackLengthInSec = intent.getIntExtra("length", 0);
        } else if (action.equals(AndroidMusicReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            boolean playing = intent.getBooleanExtra("playing", false);
            int positionInMs = intent.getIntExtra("playbackPosition", 0);
        } else if (action.equals(AndroidMusicReceiver.BroadcastTypes.QUEUE_CHANGED)) {
        }
    }


}
