package com.gulshansingh.hackerlivewallpaper.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.gulshansingh.hackerlivewallpaper.R;

import java.util.Arrays;
import java.util.List;

public class CharacterSetPreference extends DialogPreference {

    private EditText editText;
    private Spinner spinner;

    public CharacterSetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_dialog_character_set);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        String characterSetName = getSharedPreferences().getString("character_set_name", "Binary");
        updateEditText(characterSetName);
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        SharedPreferences sp = getSharedPreferences();
        String characterSetName = sp.getString("character_set_name", "Binary");

        Resources resources = view.getContext().getResources();
        List<String> characterSets = Arrays.asList(resources.getStringArray(R.array.character_sets));

        editText = (EditText) view.findViewById(R.id.preference_character_set);
        editText.addTextChangedListener(new TextWatcher() {
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

        spinner = (Spinner) view.findViewById(R.id.preference_character_set_name);
        spinner.setSelection(characterSets.indexOf(characterSetName));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String characterSetName = spinner.getSelectedItem().toString();
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
        String characterSet;
        if (characterSetName.equals("Binary")) {
            characterSet = "01";
            editText.setEnabled(false);
        } else if (characterSetName.equals("Matrix")) {
            characterSet = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ";
            editText.setEnabled(false);
        } else if (characterSetName.equals("Custom")) {
            editText.setEnabled(true);
            characterSet = getSharedPreferences().getString("custom_character_set", "");
            disablePosButton(characterSet.length() == 0);
        } else {
            throw new RuntimeException("Invalid character set name");
        }

        editText.setText(characterSet);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            String characterSetName = spinner.getSelectedItem().toString();
            editor.putString("character_set_name", characterSetName);
            if (characterSetName.equals("Custom")) {
                editor.putString("custom_character_set", editText.getText().toString());
            }
            editor.commit();
            setSummary("Character set is " + characterSetName);
        }
    }
}
