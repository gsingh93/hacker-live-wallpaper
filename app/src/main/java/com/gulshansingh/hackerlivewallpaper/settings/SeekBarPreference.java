package com.gulshansingh.hackerlivewallpaper.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gulshansingh.hackerlivewallpaper.R;
import com.gulshansingh.hackerlivewallpaper.Refreshable;

public abstract class SeekBarPreference extends DialogPreference implements
		Refreshable {

	protected int currentVal;

	/** The value the preference could possibly be once the user presses ok */
	protected int possibleVal;

	protected int maxVal = 100;
	protected int minVal = 0;

	protected String key;

	private int defaultVal = 0;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);

		for (int i = 0; i < a.getIndexCount(); i++) {
			int attr = a.getIndex(i);
			switch (attr) {
                case R.styleable.SeekBarPreference_android_key:
                    key = a.getString(attr);
                    break;
                case R.styleable.SeekBarPreference_android_defaultValue:
                    defaultVal = a.getInteger(attr, defaultVal);
                    break;
            }
		}
		a.recycle();

		currentVal = preferences.getInt(key, defaultVal);
		possibleVal = currentVal;

		a = context
				.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);

		for (int i = 0; i < a.getIndexCount(); i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.SeekBarPreference_mymin:
				minVal = a.getInteger(R.styleable.SeekBarPreference_mymin,
						minVal);
				break;
			case R.styleable.SeekBarPreference_mymax:
				maxVal = a.getInteger(R.styleable.SeekBarPreference_mymax,
						maxVal);
				break;
			}
		}
		a.recycle();

		// The seek bar must start at 0, so we have to scale max downward
		// and account for this later on
		maxVal -= minVal;

		setDialogLayoutResource(R.layout.preference_dialog_number_picker);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		setSummary(transform(currentVal));
		setDialogIcon(null);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			if (key != null) {
				currentVal = possibleVal;
				setSummary(transform(currentVal));
				persistInt(currentVal);
			}
		}
	}

	@Override
	public void refresh(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		currentVal = preferences.getInt(key, defaultVal);
		setSummary(transform(currentVal));
	}

	protected abstract String transform(int value);

	@Override
	protected void onBindDialogView(@NonNull View view) {
		super.onBindDialogView(view);

		SeekBar seekBar = (SeekBar) view.findViewById(R.id.preference_seek_bar);
		seekBar.setMax(maxVal);
		seekBar.setProgress(currentVal - minVal);

		final TextView progressView = (TextView) view
				.findViewById(R.id.preference_seek_bar_progress);
		progressView.setText(transform(currentVal));
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				possibleVal = progress + minVal;
				progressView.setText(transform(possibleVal));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}
}
