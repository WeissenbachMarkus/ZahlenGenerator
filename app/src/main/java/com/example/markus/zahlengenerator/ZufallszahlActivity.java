package com.example.markus.zahlengenerator;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;
import static com.example.markus.zahlengenerator.NumberContract.NumberEntry.*;


public class ZufallszahlActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LIFECYCLE_CALLBACK_TEXT_KEY="callback";
    private TextView randomNumberView;
    private Button generateNumberButton;
    private Random rand;
    private int rangeStart, rangeEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zahlen_generator);

        this.randomNumberView = (TextView) findViewById(R.id.randomNumber);
        this.generateNumberButton = (Button) findViewById(R.id.generateButton);
        this.rand = new Random();

        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey(LIFECYCLE_CALLBACK_TEXT_KEY))
            {
             this.randomNumberView.setText(savedInstanceState.getString(LIFECYCLE_CALLBACK_TEXT_KEY));
            }
        }

        setupSharedPreferences();

        this.generateNumberButton.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             generateNumber();
                                                         }
                                                     }
        );

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(LIFECYCLE_CALLBACK_TEXT_KEY, this.randomNumberView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.rangeStart = Integer.parseInt(preferences.getString(getString(R.string.pref_rangeStart), getString(R.string.defaultRangeStart)));
        this.rangeEnd = Integer.parseInt(preferences.getString(getString(R.string.pref_rangeEnd), getString(R.string.defaultRangeEnd)));

        preferences.registerOnSharedPreferenceChangeListener(this);

    }

    private void generateNumber() {

        int randomNumber=this.rand.nextInt(this.rangeEnd) + this.rangeStart;

        ContentValues values= new ContentValues();
        values.put(COLUMN_VALUE,randomNumber);

        Uri uri=getContentResolver().insert(CONTENT_URI,values);


        this.randomNumberView
                .setText(String.valueOf(randomNumber));

    }

   @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (getString(R.string.pref_rangeStart).equals(key))
            this.rangeStart = Integer.parseInt(sharedPreferences.getString(key, getString(R.string.defaultRangeStart)));
        else if (getString(R.string.pref_rangeEnd).equals(key))
            this.rangeEnd = Integer.parseInt(sharedPreferences.getString(key, getString(R.string.defaultRangeEnd)));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


}
