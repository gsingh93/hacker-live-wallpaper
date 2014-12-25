package com.gulshansingh.hackerlivewallpaper.settings;

import android.content.Context;
import android.util.AttributeSet;

public class PercentSeekBarPreference extends SeekBarPreference {

	public PercentSeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected String transform(int value) {
		return value + "%";
	}
}
