package se.lublin.mumla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import se.lublin.mumla.service.ipc.TalkBroadcastReceiver;

public class PttBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PttBroadcastReceiver";
    private static final String ACTION_PTT_DOWN = "android.intent.action.PTT.down";
    private static final String ACTION_PTT_UP = "android.intent.action.PTT.up";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        Log.d(TAG, "Received intent: " + intent.getAction());

        Intent talkIntent = new Intent(TalkBroadcastReceiver.BROADCAST_TALK);

        switch (intent.getAction()) {
            case ACTION_PTT_DOWN:
                talkIntent.putExtra(TalkBroadcastReceiver.EXTRA_TALK_STATE, true);
                context.sendBroadcast(talkIntent);
                break;
            case ACTION_PTT_UP:
                talkIntent.putExtra(TalkBroadcastReceiver.EXTRA_TALK_STATE, false);
                context.sendBroadcast(talkIntent);
                break;
        }
    }
}

