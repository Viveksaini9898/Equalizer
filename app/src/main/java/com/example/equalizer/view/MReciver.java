/*
package com.example.equalizer.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public abstract class MReciver extends BroadcastReceiver {

    public final String albumId = "albumId";

    public final String artist = "artist";

    public final String playing = "playing";

    public final String track = "track";

    protected Context context;

    protected asd f3676f;

    protected final String string;
    protected final String string1;

    public MReciver(String str, String str2) {
        this.string = str;
        this.string1 = str2;
    }


    public C1988yj mo17211a(String str, Bundle bundle) {
        boolean z;
        String string = bundle.getString("android.media.extra.PACKAGE_NAME");
        if (string == null || !string.equals("com.meizu.media.music")) {
            z = bundle.getBoolean("playing");
        } else {
            String string2 = bundle.getString("playstate");
            z = string2 != null && string2.equals("3");
        }
        String string3 = bundle.getString("artist");
        String string4 = bundle.getString("track");
        long j = bundle.getLong("albumId");
        if (!(string3 == null && string4 == null)) {
            this.f3676f = new C1990yk(j, string3, string4);
        }
        return new C1988yj(this.f3676f, z, this.f3678h);
    }

    public final void onReceive(Context context, Intent intent) {
        SharedPreferences.Editor edit;
        new StringBuilder("===Action received was:").append(intent.getAction());
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        if (extras != null) {
            this.context = context;
            String str = "The list of keys : ";
            for (String str2 : extras.keySet()) {
                str = str + "['" + str2 + "'='" + extras.get(str2) + "'], ";
            }
            new StringBuilder("===").append(str.toString());
            if (action != null && extras != null) {
                C2008yx a = C2008yx.m4077a(this.context);
                C1988yj b = a.mo18986b();
                if (b != null) {
                    this.f3676f = b.f3664d;
                }
                C1988yj a2 = mo17211a(action, extras);
                if (a2 != null) {
                    a2.f3662b = a.mo18987c();
                    String d = a.mo18988d();
                    if (d != null) {
                        a2.f3661a = d;
                    }
                    List<C2002ys.C2003a> list = C2002ys.m4071a().f3702a;
                    for (C2002ys.C2003a next : list) {
                        if (next != null) {
                            next.mo17238a(a2);
                        }
                    }
                    if (a2 == null) {
                        edit = a.f3731a.edit();
                        edit.clear();
                    } else {
                        edit = a.f3731a.edit();
                        edit.putString("player_pkg_name", a2.f3661a);
                        edit.putBoolean("player_state", a2.f3663c);
                        C1990yk ykVar = a2.f3664d;
                        if (ykVar != null) {
                            edit.putString("track_title", ykVar.f3668c);
                            edit.putString("track_artist", ykVar.f3667b);
                            edit.putLong("track_album_id", ykVar.f3666a);
                        }
                    }
                    edit.commit();
                    if (!a2.f3663c) {
                        C2008yx.m4077a(this.f3675e).mo18985a();
                        Iterator<C2002ys.C2003a> it = list.iterator();
                        while (it.hasNext()) {
                            it.next();
                        }
                    }
                }
            }
        }
    }
}
*/
