package com.gulshansingh.hackerlivewallpaper.settings;

import org.holoeverywhere.preference.DialogPreference;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.SeekBar.OnSeekBarChangeListener;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.gulshansingh.hackerlivewallpaper.R;

public class SeekBarPreference extends DialogPreference {

	private static final String androidNS = "http://schemas.android.com/apk/res/android";
	private static final String holoNS = "http://schemas.android.com/apk/res-auto";

	private int currentVal;
	private int maxVal;

	private String key;

	private Context context;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.context = context;
		key = attrs.getAttributeValue(holoNS, "key");
		currentVal = preferences.getInt(key, -1);
		if (currentVal == -1) {
			currentVal = attrs.getAttributeIntValue(holoNS, "defaultValue", 0);
		}
		maxVal = attrs.getAttributeIntValue(androidNS, "max", 100);

		setDialogLayoutResource(R.layout.preference_dialog_number_picker);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		setSummary(String.valueOf(currentVal));
		setDialogIcon(null);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (key != null) {
			setSummary(String.valueOf(currentVal));
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(context).edit();
			editor.putInt(key, currentVal);
			editor.commit();
		}
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		final TextView progressView = (TextView) view
				.findViewById(R.id.preference_seek_bar_progress);
		SeekBar seekBar = (SeekBar) view.findViewById(R.id.preference_seek_bar);
		seekBar.setMax(maxVal);
		seekBar.setProgress(currentVal);
		progressView.setText(String.valueOf(currentVal));
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				currentVal = progress;
				progressView.setText(String.valueOf(progress));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}
}