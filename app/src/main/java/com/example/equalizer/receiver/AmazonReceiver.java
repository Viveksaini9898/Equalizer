package com.example.equalizer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AmazonReceiver extends BroadcastReceiver {
    static final class BroadcastTypes {
        static final String AMAZON_PACKAGE = "com.amazon.mp3";
        static final String PLAYBACK_STATE_CHANGED = AMAZON_PACKAGE + ".playbackstatechanged";
        static final String QUEUE_CHANGED = AMAZON_PACKAGE + ".queuechanged";
        static final String METADATA_CHANGED = AMAZON_PACKAGE + ".metadatachanged";
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Amazon",Toast.LENGTH_LONG).show();
        // This is sent with all broadcasts, regardless of type. The value is taken from
        // System.currentTimeMillis(), which you can compare to in order to determine how
        // old the event is.
        long timeSentInMs = intent.getLongExtra("timeSent", 0L);

        String action = intent.getAction();

        if (action.equals(AmazonReceiver.BroadcastTypes.METADATA_CHANGED)) {
            String trackId = intent.getStringExtra("id");
            String artistName = intent.getStringExtra("artist");
            String albumName = intent.getStringExtra("album");
            String trackName = intent.getStringExtra("track");
            int trackLengthInSec = intent.getIntExtra("length", 0);
            // Do something with extracted information...
        } else if (action.equals(AmazonReceiver.BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            boolean playing = intent.getBooleanExtra("playing", false);
            int positionInMs = intent.getIntExtra("playbackPosition", 0);
            // Do something with extracted information
        } else if (action.equals(AmazonReceiver.BroadcastTypes.QUEUE_CHANGED)) {
            // Sent only as a notification, your app may want to respond accordingly.
        }
    }

}
