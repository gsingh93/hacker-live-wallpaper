package com.gulshansingh.hackerlivewallpaper;

import org.holoeverywhere.preference.PreferenceActivity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gulshansingh.hackerlivewallpaper.settings.SeekBarPreference;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_reset_to_defaults) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear();
			editor.commit();
			refreshPreferences();
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void refreshPreferences() {
		String[] keys = { "num_bits", "falling_speed", "change_bit_speed",
				"text_size" };
		for (String key : keys) {
			((SeekBarPreference) getPreferenceScreen().findPreference(key))
					.refresh(this);
		}
	}
}
