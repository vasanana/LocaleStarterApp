package com.example.localetextstarter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private int mInputQuantity = 1;

    private NumberFormat mNumberFormat = NumberFormat.getInstance();
    private static final String TAG = MainActivity.class.getSimpleName();

    private double mPrice = 0.10;

    private double mFrExchangeRate = 0.93;
    private double mIwExchangeRate = 3.61;

    private NumberFormat mCurrencyFormat = NumberFormat.getCurrencyInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelp();
            }
        });

        Date myDate = new Date();
        long expirationDate = myDate.getTime() + TimeUnit.DAYS.toMillis(5);
        myDate.setTime(expirationDate);

        String formatDate = DateFormat.getDateInstance().format(myDate);
        TextView expiredTextView = findViewById(R.id.date);
        expiredTextView.setText(formatDate);

        String formatPrice;
        String deviceLocale = Locale.getDefault().getCountry();
        if (deviceLocale.equals("FR") || deviceLocale.equals("IL")){
            if (deviceLocale.equals("FR")){
                mPrice *= mFrExchangeRate;
            } else {
                mPrice *= mIwExchangeRate;
            }
            formatPrice = mCurrencyFormat.format(mPrice);
        } else {
            mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            formatPrice = mCurrencyFormat.format(mPrice);
        }

        TextView localePrice = findViewById(R.id.price);
        localePrice.setText(formatPrice);

        EditText enteredQuantity = findViewById(R.id.quantity);
        enteredQuantity.setOnEditorActionListener
                (new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(textView.getWindowToken(),0);

                            try {
                                mInputQuantity = mNumberFormat.parse(textView.getText().toString()).intValue();
                                textView.setError(null);
                            } catch (ParseException e) {
                                Log.e(TAG, Log.getStackTraceString(e));
                                textView.setError(getText(R.string.enter_number));
                                return false;
                            }

                            String formatQuantity = mNumberFormat.format(mInputQuantity);
                            textView.setText(formatQuantity);
                            return true;
                        }
                        return false;
                    }
                });
    }

    /**
     * Shows the Help screen.
     */
    private void showHelp() {
        // Create the intent.
        Intent helpIntent = new Intent(this, HelpActivity.class);
        // Start the HelpActivity.
        startActivity(helpIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((EditText) findViewById(R.id.quantity)).getText().clear();
    }

    /**
     * Creates the options menu and returns true.
     *
     * @param menu       Options menu
     * @return boolean   True after creating options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles options menu item clicks.
     *
     * @param item      Menu item
     * @return boolean  True if menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle options menu item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_language){
            Intent languageIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(languageIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}