/*
  Copyright © 2017 Jeremy March. All rights reserved.

This file is part of philologus-Android.

    philologus-Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <https://www.gnu.org/licenses/>.

 */

package com.philolog.philologus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.philolog.philologus.database.PHDBHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordListActivity extends FragmentActivity implements
        WordListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    ProgressBar pgsBar;
    public boolean mTwoPane;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean barShown = false;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;


    public static void localSetTheme(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = sharedPref.getString("PHTheme", "PHDayNight");

        switch(themeName)
        {
            case "PHDark":
                context.setTheme(R.style.PHDark);
                break;
            case "PHLight":
                context.setTheme(R.style.PHLight);
                break;
            default:
                context.setTheme(R.style.PHDayNight);
                break;
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("twoPane", mTwoPane);
    }

    public void onRestoreInstanceState(Bundle inState) {
        mTwoPane = inState.getBoolean("twoPane");
    }

    private void loadDatabase() {
        pgsBar = findViewById(R.id.dbprogressbar);
        barShown = false;
        long totalFileBytes = 188469248; // This seems to be a hardcoded value for progress calculation

        executor.execute(() -> {
            // Background work
            PHDBHandler.mProgListener = progress -> {
                // Update progress
                handler.post(() -> {
                    if (!barShown) {
                        LinearLayout l = findViewById(R.id.progressContainer);
                        l.setVisibility(View.VISIBLE);
                        FrameLayout l2 = findViewById(R.id.hideduringcopy);
                        l2.setVisibility(View.GONE);
                        barShown = true;
                    }

                    double percent = (double) progress / totalFileBytes * 100;
                    long percentLong = Math.round(percent);
                    pgsBar.setProgress((int) percentLong);
                });
            };
            PHDBHandler.getInstance(this).getReadableDatabase();

            // Post-execution
            handler.post(() -> {
                LinearLayout l = findViewById(R.id.progressContainer);
                l.setVisibility(View.GONE);
                FrameLayout l2 = findViewById(R.id.hideduringcopy);
                l2.setVisibility(View.VISIBLE);
            });
        });
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        localSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        prefListener = (prefs, key) -> {
            if (key != null && key.equals("PHTheme")) {
                recreate();
            }
        };

        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);

        // Install databases if necessary.
        loadDatabase();

        WordListFragment wordListFragment = (WordListFragment) getSupportFragmentManager().findFragmentById(R.id.word_list);

        if (findViewById(R.id.word_detail_container) != null) {
            mTwoPane = true;

            if (wordListFragment != null) {
                wordListFragment.setActivateOnItemClick(true);
            }
        }

        if (wordListFragment != null) {
            wordListFragment.onTwoPaneChanged(mTwoPane);
        }
    }
    public void clearSearch(View v)
    {
        EditText s = findViewById(R.id.word_search);
        s.setText("");
    }

    @Override
    public void onItemSelected(long id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putLong(WordDetailFragment.ARG_ITEM_ID, id);
            WordDetailFragment fragment = new WordDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.word_detail_container, fragment).commit();

        } else {
            Intent detailIntent = new Intent(this, WordDetailActivity.class);
            detailIntent.putExtra(WordDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(prefListener);
    }
}
