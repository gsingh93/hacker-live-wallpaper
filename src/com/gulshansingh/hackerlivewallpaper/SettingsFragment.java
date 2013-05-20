package com.gulshansingh.hackerlivewallpaper;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import org.holoeverywhere.preference.PreferenceFragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gulshansingh.hackerlivewallpaper.settings.SeekBarPreference;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		addPreferencesFromResource(R.xml.prefs);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_settings, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_reset_to_defaults) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
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
					.refresh(getActivity());
		}
		ColorPickerPreference bitPref = (ColorPickerPreference) findPreference("bit_color");
		int defaultColor = getResources().getColor(R.color.green);
		bitPref.onColorChanged(defaultColor);

		ColorPickerPreference backgroundPref = (ColorPickerPreference) findPreference("background_color");
		defaultColor = getResources().getColor(R.color.black);
		backgroundPref.onColorChanged(defaultColor);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BitSequence.configure(getActivity());
	}
}
