package com.gulshansingh.hackerlivewallpaper.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.gulshansingh.hackerlivewallpaper.R;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceManager;

public class CharacterSetPreferenceDialogFragment extends PreferenceDialogFragmentCompat {
    public static final String BINARY_CHAR_SET = "01";
    public static final String MATRIX_CHAR_SET = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ";

    private EditText mEditText;
    private Spinner mSpinner;

    private Preference mPreference;

    public CharacterSetPreferenceDialogFragment(Preference preference) {
        mPreference = preference;

        final Bundle b = new Bundle();
        b.putString(ARG_KEY, mPreference.getKey());
        setArguments(b);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String characterSetName = sp.getString("character_set_name", "Binary");

        Resources resources = getContext().getResources();
        List<String> characterSets =
                Arrays.asList(resources.getStringArray(R.array.character_sets));

        mEditText = view.findViewById(R.id.preference_character_set);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                disablePosButton(s.length() == 0);
            }
        });

        mSpinner = view.findViewById(R.id.preference_character_set_name);
        mSpinner.setSelection(characterSets.indexOf(characterSetName));
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String characterSetName = mSpinner.getSelectedItem().toString();
                updateEditText(characterSetName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void disablePosButton(boolean disable) {
        Button posButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        if (disable) {
            posButton.setEnabled(false);
        } else {
            posButton.setEnabled(true);
        }
    }

    private void updateEditText(String characterSetName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        String characterSet;
        if (characterSetName.equals("Binary")) {
            characterSet = BINARY_CHAR_SET;
            mEditText.setEnabled(false);
        } else if (characterSetName.equals("Matrix")) {
            characterSet = MATRIX_CHAR_SET;
            mEditText.setEnabled(false);
        } else if (characterSetName.equals("Custom (random characters)")) {
            mEditText.setEnabled(true);
            characterSet = sp.getString("custom_character_set", "");
            disablePosButton(characterSet.length() == 0);
        } else if (characterSetName.equals("Custom (exact text)")) {
            mEditText.setEnabled(true);
            characterSet = sp.getString("custom_character_string", "");
            disablePosButton(characterSet.length() == 0);
        } else {
            if (!characterSetName.equals("Custom")) { // Legacy charset name
                throw new RuntimeException("Invalid character set " + characterSetName);
            } else {
                sp.edit().putString("character_set_name", "Custom (random characters)")
                        .apply();
                mEditText.setEnabled(true);
                characterSet = sp.getString("custom_character_set", "");
                disablePosButton(characterSet.length() == 0);
            }
        }

        mEditText.setText(characterSet);
    }

    @Override
    protected View onCreateDialogView(Context context) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        return inflater.inflate(R.layout.preference_dialog_character_set, null);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            String characterSetName = mSpinner.getSelectedItem().toString();
            editor.putString("character_set_name", characterSetName);
            if (characterSetName.equals("Custom (random characters)")) {
                editor.putString("custom_character_set", mEditText.getText().toString());
            } else if (characterSetName.equals("Custom (exact text)")) {
                editor.putString("custom_character_string", mEditText.getText().toString());
            }
            editor.apply();
            mPreference.setSummary("Character set is " + characterSetName);
        }
    }
}