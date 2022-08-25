package com.eivind.prosjekt;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Pref extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);

        Intent i = new Intent(this, TetrisActivity.class);
        startActivity(i);
        finish();
    }
}
