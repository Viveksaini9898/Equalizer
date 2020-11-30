package com.example.equalizer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MIUIReceiver extends BroadcastReceiver {

    static final class BroadcastTypes {
        static final String MIUI_PACKAGE = "com.miui.player";
        static final String PLAYBACK_STATE_CHANGED =  MIUI_PACKAGE + ".playstatechanged";
        static final String QUEUE_CHANGED = MIUI_PACKAGE + ".playbackcomplete";
        static final String METADATA_CHANGED = MIUI_PACKAGE + ".metachanged";
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"Miui",Toast.LENGTH_LONG).show();
        long timeSentInMs = intent.getLongExtra("timeSent", 0L);

        String action = intent.getAction();

        if (action.equals(MIUIReceiver.BroadcastTypes.METADATA_CHANGED)) {
            String trackId = intent.getStringExtra("id");
            String artistName = intent.getStringExtra("artist");
            String albumName = intent.getStringExtra("album");
            String trackName = intent.getStringExtra("track");
            int trackLengthInSec = intent.getIntExtra("length", 0);
        } else if (action.equals(MIUIReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            boolean playing = intent.getBooleanExtra("playing", false);
            int positionInMs = intent.getIntExtra("playbackPosition", 0);
        } else if (action.equals(MIUIReceiver.BroadcastTypes.QUEUE_CHANGED)) {
        }
    }
}
