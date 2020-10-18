package com.gulshansingh.hackerlivewallpaper.settings;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.PreferenceViewHolder;
import androidx.preference.SeekBarPreference;

public class PercentSeekBarPreference extends SeekBarPreference {
    private TextView mSeekBarValueTextView;

    public PercentSeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private View findViewByClass(LinearLayout layout, String className) throws ClassNotFoundException {
        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = layout.getChildAt(i);
            if (Class.forName(className).isInstance(v)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        try {
            // TODO: Improve how we find the text view
            LinearLayout nested_layout =
                    (LinearLayout) findViewByClass((LinearLayout) ((LinearLayout) view.itemView).getChildAt(1), "android.widget.LinearLayout");
            if (nested_layout == null) {
                throw new RuntimeException("Unable to find LinearLayout in SeekBarPreference");
            }
            mSeekBarValueTextView = (TextView) findViewByClass(
                    nested_layout,
                    "android.widget.TextView");
            if (mSeekBarValueTextView == null) {
                throw new RuntimeException("Unable to find TextView in SeekBarPreference");
            }

            // Whenever the TextView value changes, add a '%' after the value
            mSeekBarValueTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Removing and adding the TextWatcher prevents infinite recursion
                    mSeekBarValueTextView.removeTextChangedListener(this);
                    s.append("%");
                    mSeekBarValueTextView.addTextChangedListener(this);
                }
            });

            // Triggers the TextWatcher so the initial text shows a '%' symbol
            mSeekBarValueTextView.setText(mSeekBarValueTextView.getText());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
