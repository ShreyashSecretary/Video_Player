package hb.xvideoplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil {
    private static final String LAST_SPEED = "last_speed";
    private static final String LAST_BRIGHTNESS = "last_brightness";
    private static final String SORT_ORDER = "sort_order";
    private static final String LOCK = "lock";
    private static final String RATE_US = "rate_us";
    private static PreferenceUtil sInstance;
    private final SharedPreferences mPreferences;

    private PreferenceUtil( Context context) {
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance( Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    public void saveLastBrightness(float f) {
        this.mPreferences.edit().putFloat(LAST_BRIGHTNESS, f).apply();
    }

    public float getLastBrightness() {
        return this.mPreferences.getFloat(LAST_BRIGHTNESS, 0.5f);
    }

    public void saveLastSpeed(float f) {
        this.mPreferences.edit().putFloat(LAST_SPEED, f).apply();
    }

    public float getLastSpeed() {
        return this.mPreferences.getFloat(LAST_SPEED, 1.0f);
    }

    public void saveSortOrder(int x) {
        this.mPreferences.edit().putInt(SORT_ORDER, x).apply();
    }

    public int getSortOrder() {
        return this.mPreferences.getInt(SORT_ORDER, 0);
    }

    public boolean getRate() {
        return this.mPreferences.getBoolean(RATE_US, false);
    }

    public void setRate(Boolean x) {
        this.mPreferences.edit().putBoolean(RATE_US, x).apply();
    }

    public boolean getLock() {
        return this.mPreferences.getBoolean(LOCK, false);
    }

    public void setLock(Boolean x) {
        this.mPreferences.edit().putBoolean(LOCK, x).apply();
    }

    public void setLastPlayerDuration(String key, long value) {
        this.mPreferences.edit().putLong(key, value).apply();
    }

    public long getLastPlayerDuration(String key) {
        return this.mPreferences.getLong(key, 0);
    }

}
