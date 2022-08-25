package com.eivind.prosjekt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TetrisActivity extends AppCompatActivity {
    public static String TAG = "test";
    public static int LEFT = -1;
    public static int RIGHT = 1;
    private com.eivind.prosjekt.TetrisView mTetrisView;
    private String mFinish;
    private String mPause;
    private String mRestart;
    private String mSettings;
    private String mAlert;
    Button right;
    Button left;
    Button rotate;
    String curLang = "no";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String lang = SP.getString("language", "no");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config =
                getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_tetris);
        mTetrisView = (TetrisView)findViewById(R.id.tetrisView);
        Resources res = getResources();
        mFinish = res.getString(R.string.finish);
        mPause=res.getString(R.string.pause);
        mRestart=res.getString(R.string.resume);
        mSettings=res.getString(R.string.menu_settings);
        mAlert = res.getString(R.string.help);


        right = findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisView.moveRight();
            }
        });
        left = findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisView.moveLeft();
            }
        });
        rotate = findViewById(R.id.rotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTetrisView.rotate90Clockwise(mTetrisView.shapes[mTetrisView.n]);
            }
        });

    }
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(mFinish);
        menu.add(mPause);
        menu.add(mRestart);
        menu.add(mSettings);
        menu.add(mAlert);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().equals(mFinish)){
            mTetrisView.getThread().setRunning(false);
            finish();
        }
        else if (item.getTitle().equals(mPause)) {
            mTetrisView.getThread().setPaused(true);
        }
        else if (item.getTitle().equals(mRestart)) {
            mTetrisView.getThread().setPaused(false);
            mTetrisView.setFocusable(true); // make sure we get key events
        }else if(item.getTitle().equals(mSettings)){
            Intent i = new Intent(this, Pref.class);
            startActivity(i);
            finish();
        }else if(item.getTitle().equals(mAlert)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.info)
                    .setMessage(R.string.alert)

                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        return true;
    }
    protected void onPause() {
        super.onPause();
        mTetrisView.getThread().setPaused(true); // pause game when Activity pauses
    }

}
