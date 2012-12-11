package com.gulshansingh.hackerlivewallpaper.settings;

import org.holoeverywhere.preference.DialogPreference;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.SeekBar.OnSeekBarChangeListener;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.gulshansingh.hackerlivewallpaper.R;

public abstract class SeekBarPreference extends DialogPreference {

	protected int possibleVal;
	protected int currentVal;
	protected int maxVal;
	protected int minVal;

	protected String key;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Preference);
		
		key = a.getString(R.styleable.Preference_key);
		currentVal = preferences.getInt(key, -1);
		if (currentVal == -1) {
			currentVal = a.getInteger(R.styleable.Preference_defaultValue, 0);
		}
		a.recycle();

		a = context
				.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
		minVal = a.getInt(R.styleable.SeekBarPreference_mymin, 0);
		maxVal = a.getInt(R.styleable.SeekBarPreference_mymax, 100);
		maxVal -= minVal;
		a.recycle();

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

	protected abstract String transform(int value);

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		SeekBar seekBar = (SeekBar) view.findViewById(R.id.preference_seek_bar);
		seekBar.setMax(maxVal);
		seekBar.setProgress(currentVal - minVal);

		final TextView progressView = (TextView) view
				.findViewById(R.id.preference_seek_bar_progress);
		progressView.setText(transform(currentVal));
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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