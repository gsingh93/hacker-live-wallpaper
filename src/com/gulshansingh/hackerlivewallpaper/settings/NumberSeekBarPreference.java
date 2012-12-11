package com.gulshansingh.hackerlivewallpaper.settings;

import android.content.Context;
import android.util.AttributeSet;

public class NumberSeekBarPreference extends SeekBarPreference {

	public NumberSeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected String transform(int value) {
		return String.valueOf(value);
	}

}
