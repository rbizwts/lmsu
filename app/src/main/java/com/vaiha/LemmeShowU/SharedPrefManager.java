package com.vaiha.LemmeShowU;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "LemmeShowU";
    private static final String KEY_IS_PEMISSION = "isGrantPermission";
    private static String TAG = SharedPrefManager.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    public SharedPrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setGrantPermission(boolean isGrantPermission) {
        editor.putBoolean(KEY_IS_PEMISSION, isGrantPermission);
        editor.commit();
    }

    public boolean isGrantPermission() {
        return pref.getBoolean(KEY_IS_PEMISSION, false);
    }
}
