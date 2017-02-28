package com.example.markus.zahlengenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;


/**
 * Created by markus on 25.02.2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    EditTextPreference rangeStart, rangeEnd;

   @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_zufallszahl);

        this.rangeStart = (EditTextPreference) getPreferenceScreen()
                .findPreference(getString(R.string.pref_rangeStart));
        this.rangeEnd = (EditTextPreference) getPreferenceScreen()
                .findPreference(getString(R.string.pref_rangeEnd));

        this.rangeStart.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object newValue) {
                test(preference.getKey(), newValue, preference.getContext());
                return false;
            }
        });
        this.rangeEnd.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object newValue) {

                return  test(preference.getKey(), newValue, preference.getContext());
            }
        });


    }

    private boolean test(String key, Object newValue, Context context) {


        try {
            Integer.parseInt(String.valueOf(newValue));

        } catch (java.lang.NumberFormatException ex) {
            Toast.makeText(context, R.string.numbersValid, Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

            if (key.equals(getString(R.string.pref_rangeStart))) {
                editor.putString(key, getString(R.string.defaultRangeStart));
            } else if (key.equals(getString(R.string.pref_rangeEnd)))
                editor.putString(key, getString(R.string.defaultRangeEnd));

            editor.commit();
            return false;
        }

        return true;

    }

}
