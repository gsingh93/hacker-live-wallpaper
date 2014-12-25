package com.gulshansingh.hackerlivewallpaper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    public static final String KEY_BACKGROUND_COLOR = "background_color";
    public static final String KEY_ENABLE_DEPTH = "enable_depth";
    public static final String KEY_TEXT_SIZE = "text_size";
    public static final String KEY_CHANGE_BIT_SPEED = "change_bit_speed";
    public static final String KEY_FALLING_SPEED = "falling_speed";
    public static final String KEY_NUM_BITS = "num_bits";
    public static final String KEY_BIT_COLOR = "bit_color";

    /** Keys for preferences that should be refreshed */
    private static final List<String> mRefreshKeys = Arrays.asList(
            KEY_NUM_BITS, KEY_FALLING_SPEED, KEY_CHANGE_BIT_SPEED,
            KEY_TEXT_SIZE);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_reset_to_defaults) {
            resetToDefaults();
            refreshPreferences();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void resetToDefaults() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
    }

    private void refreshPreferences() {
        for (String key : mRefreshKeys) {
            ((Refreshable) getPreferenceScreen().findPreference(key))
                    .refresh(this);
        }

        ColorPickerPreference bitPref = (ColorPickerPreference) findPreference(KEY_BIT_COLOR);
        int defaultColor = getResources().getColor(R.color.default_bit_color);
        bitPref.onColorChanged(defaultColor);

        ColorPickerPreference backgroundPref = (ColorPickerPreference) findPreference(KEY_BACKGROUND_COLOR);
        defaultColor = getResources()
                .getColor(R.color.default_background_color);
        backgroundPref.onColorChanged(defaultColor);

        CheckBoxPreference depthEnabledPref = (CheckBoxPreference) findPreference(KEY_ENABLE_DEPTH);
        depthEnabledPref.setChecked(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BitSequence.configure(this);
        HackerWallpaperService.reset();
    }
}
