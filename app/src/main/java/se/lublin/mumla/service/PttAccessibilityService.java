package se.lublin.mumla.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import se.lublin.mumla.Settings;
import se.lublin.mumla.service.ipc.TalkBroadcastReceiver;

public class PttAccessibilityService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "PttAccessibilityService";
    private int pttKeyCode;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        loadPttKey(preferences);
        Log.d(TAG, "PTT Accessibility Service created. PTT KeyCode: " + pttKeyCode);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Mumla::PttWakeLock");
        wakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void loadPttKey(SharedPreferences sharedPreferences) {
        pttKeyCode = sharedPreferences.getInt(Settings.PREF_PUSH_KEY, Settings.DEFAULT_PUSH_KEY);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (pttKeyCode == -1) {
            // PTT key is not configured
            return super.onKeyEvent(event);
        }

        if (event.getKeyCode() == pttKeyCode) {
            Intent intent = new Intent(TalkBroadcastReceiver.BROADCAST_TALK);
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d(TAG, "PTT Key Down");
                intent.putExtra(TalkBroadcastReceiver.EXTRA_TALK_STATE, true);
                sendBroadcast(intent);
                return true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                Log.d(TAG, "PTT Key Up");
                intent.putExtra(TalkBroadcastReceiver.EXTRA_TALK_STATE, false);
                sendBroadcast(intent);
                return true;
            }
        }
        return super.onKeyEvent(event);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not used
    }

    @Override
    public void onInterrupt() {
        // Not used
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Settings.PREF_PUSH_KEY.equals(key)) {
            loadPttKey(sharedPreferences);
            Log.d(TAG, "PTT KeyCode updated to: " + pttKeyCode);
        }
    }
}
