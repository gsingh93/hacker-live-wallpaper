package com.gulshansingh.hackerlivewallpaper;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gulshansingh.hackerlivewallpaper.settings.CharacterSetPreference;
import com.gulshansingh.hackerlivewallpaper.settings.CharacterSetPreferenceDialogFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_BACKGROUND_COLOR = "background_color";
    public static final String KEY_ENABLE_DEPTH = "enable_depth";
    public static final String KEY_TEXT_SIZE = "text_size";
    public static final String KEY_CHANGE_BIT_SPEED = "change_bit_speed";
    public static final String KEY_FALLING_SPEED = "falling_speed";
    public static final String KEY_NUM_BITS = "num_bits";
    public static final String KEY_BIT_COLOR = "bit_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No need to call `setContentView`, the fragment will be set in `onStart`
    }

    @Override
    public void onStart() {
        super.onStart();
        // We need to restart the fragment on start because we may have opened up the settings from
        // both the launcher and the wallpaper chooser, so when we change a setting in one place we
        // want it to show up when we go back to the other place
        restartFragment();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Apply any changes to the settings and restart the wallpaper animation
        BitSequence.configure(this);
        HackerWallpaperService.reset();
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
            restartFragment();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Sets the preferences to their default values without updating the GUI
     */
    private void resetToDefaults() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .clear()
                .apply();
        PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
    }

    /**
     * Replace the existing SettingsFragment with a new SettingsFragment, causing all of the
     * preference values to be reloaded
     */
    public void restartFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.prefs);

            Preference setAsWallpaper = findPreference("set_as_wallpaper");
            setAsWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference pref) {
                    Intent i = new Intent();
                    try {
                        if (Build.VERSION.SDK_INT > 15) {
                            i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

                            String p = HackerWallpaperService.class.getPackage().getName();
                            String c = HackerWallpaperService.class.getCanonicalName();
                            i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    new ComponentName(p, c));
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
        public void onDisplayPreferenceDialog(Preference preference) {
            if (preference instanceof CharacterSetPreference) {
                final DialogFragment f = new CharacterSetPreferenceDialogFragment(preference);
                f.setTargetFragment(this, 0);
                f.show(getParentFragmentManager(),
                        CharacterSetPreferenceDialogFragment.class.getName());
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }
    }
}