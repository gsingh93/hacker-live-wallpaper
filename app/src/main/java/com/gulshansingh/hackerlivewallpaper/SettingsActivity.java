package com.gulshansingh.hackerlivewallpaper;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
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
    public static final String KEY_CHARACTER_SET_PREFS = "character_set_prefs";

    /** Keys for preferences that should be refreshed */
    private static final List<String> mRefreshKeys = Arrays.asList(
            KEY_NUM_BITS, KEY_FALLING_SPEED, KEY_CHANGE_BIT_SPEED,
            KEY_TEXT_SIZE, KEY_CHARACTER_SET_PREFS);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        PreferenceManager pm = getPreferenceManager();
        Preference characterSetPrefs = (Preference) pm.findPreference(KEY_CHARACTER_SET_PREFS);
        String characterSet = pm.getSharedPreferences().getString("character_set_name", "Binary");
        characterSetPrefs.setSummary("Character set is " + characterSet);

        Preference setAsWallpaper = (Preference) pm.findPreference("set_as_wallpaper");
        setAsWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Intent i = new Intent();
                try {
                    if (Build.VERSION.SDK_INT > 15) {
                        i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

                        String p = HackerWallpaperService.class.getPackage().getName();
                        String c = HackerWallpaperService.class.getCanonicalName();
                        i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(p, c));
                    } else {
                        i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    }
                } catch (ActivityNotFoundException e) {
                    // Fallback to the old method, some devices greater than SDK 15 are crashing
                    i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                }
                startActivity(i);
                return true;
            }
        });
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

    /** Sets the preferences to their default values without updating the GUI */
    private void resetToDefaults() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
    }

    /** Initializes the GUI to match the preferences */
    private void refreshPreferences() {
        for (String key : mRefreshKeys) {
            ((Refreshable) getPreferenceScreen().findPreference(key))
                    .refresh(this);
        }

        // We set the color to the color in the preferences to refresh the color preview
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        ColorPickerPreference bitPref = (ColorPickerPreference) findPreference(KEY_BIT_COLOR);
        int defaultColor = getResources().getColor(R.color.default_bit_color);
        int color = sp.getInt(KEY_BIT_COLOR, defaultColor);
        bitPref.onColorChanged(color);

        ColorPickerPreference backgroundPref = (ColorPickerPreference) findPreference(KEY_BACKGROUND_COLOR);
        defaultColor = getResources()
                .getColor(R.color.default_background_color);
        color = sp.getInt(KEY_BACKGROUND_COLOR, defaultColor);
        backgroundPref.onColorChanged(color);

        CheckBoxPreference depthEnabledPref = (CheckBoxPreference) findPreference(KEY_ENABLE_DEPTH);
        depthEnabledPref.setChecked(sp.getBoolean(KEY_ENABLE_DEPTH, true));
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshPreferences();
    }

    @Override
    public void onStop() {
        super.onStop();
        BitSequence.configure(this);
        HackerWallpaperService.reset();
    }
}
