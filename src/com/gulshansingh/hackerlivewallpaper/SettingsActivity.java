package com.gulshansingh.hackerlivewallpaper;

import org.holoeverywhere.preference.PreferenceActivity;

import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
	/*
	 * Preferences: - Length of Bit chains - Number Picker - Speed of bit chains
	 * - Slider? - Color of bit chains - Color picker? - Reset to defaults
	 */
}
