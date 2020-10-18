package com.gulshansingh.hackerlivewallpaper.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;

public class CharacterSetPreference extends DialogPreference {
    public static final String BINARY_CHAR_SET = "01";
    public static final String MATRIX_CHAR_SET = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ";

    private EditText mEditText;

    public CharacterSetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        String characterSet = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("character_set_name", "Binary");
        // TODO: Refactor into method
        this.setSummary("Character set is " + characterSet);
    }
}