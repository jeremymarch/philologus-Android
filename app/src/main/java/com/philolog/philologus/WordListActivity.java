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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philolog.philologus.SQLiteAssetHelper.SQLiteAssetHelper;
import com.philolog.philologus.SQLiteAssetHelper.Utils;
import com.philolog.philologus.database.PHDBHandler;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WordListActivity extends FragmentActivity implements
        WordListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    //public PHKeyboardView mKeyboardView;
    ProgressBar pgsBar;
    TextView txtView;
    int i = 0;
    Handler hdlr = new Handler();
    public boolean mTwoPane;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    public static void localSetTheme(Context context)
    {
        SharedPreferences sharedPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = sharedPref.getString("PHTheme", "PHDayNight");
        if (themeName == null)
        {
            themeName = "PHDayNight";
        }

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

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //getFragmentManager().putFragment(outState,"myfragment",myfragment);
        outState.putBoolean("twoPane", mTwoPane);
    }
    public void onRestoreInstanceState(Bundle inState){
        //myFragment = getFragmentManager().getFragment(inState,"myfragment");
        mTwoPane = inState.getBoolean("twoPane");
    }

    //https://stackoverflow.com/questions/12372759/android-get-compressed-size-of-a-file-in-a-zipfile
    //https://stackoverflow.com/questions/36045421/java-zipentry-getsize-returns-1
    public long getDBSizeFromZip(Context c, String dbFileName) {
        ZipFile zipfile = null;
        try {
            String path = c.getDatabasePath(dbFileName).getPath();
            //c.getAssets().p
            zipfile = new ZipFile(path);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        java.util.Enumeration zipEnum = zipfile.entries();

        while ( zipEnum.hasMoreElements() )
        {
            ZipEntry entry = (ZipEntry) zipEnum.nextElement();
            if ( !entry.isDirectory () && entry.getName().equals(dbFileName))
            {
                return entry.getSize(); //entry.getCompressedSize();
            }
        }
        return -1;
    }

    //used to show a message while copying over database on first load
    //params, progress, result
    private class LoadDatabaseTask extends AsyncTask<Context, Long, Void> {
        Context mContext;
        boolean barShown = false;
        long totalFileBytes = 188469248;
        //ProgressDialog mDialog;

        // Provide a constructor so we can get a Context to use to create
        // the ProgressDialog.
        public LoadDatabaseTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pgsBar = (ProgressBar) findViewById(R.id.dbprogressbar);
            //txtView = (TextView) findViewById(R.id.dbprogresstext);
            //txtView.setText("Loading database...");
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            // Copy database.
            Utils.ProgListener p = new Utils.ProgListener() {
                @Override
                public void onProgress(long progress) {
                    //Log.w("jwm", "DB Copy Progress: " + progress);
                    publishProgress(progress);
                }
            };
            PHDBHandler.mProgListener = p;
            PHDBHandler.getInstance(contexts[0]).getReadableDatabase();
            return null;
        }
        @Override
        protected void onProgressUpdate(Long... values) {
            //Log.w("jwm", "DB2 Copy Progress: " + values[0]);

            if (!barShown)
            {
                LinearLayout l = (LinearLayout) findViewById(R.id.progressContainer);
                l.setVisibility(View.VISIBLE);
                FrameLayout l2 = (FrameLayout) findViewById(R.id.hideduringcopy);
                l2.setVisibility(View.GONE);
                barShown = true;
            }

            double percent = (double)values[0] / totalFileBytes * 100;
            long percentLong = Math.round(percent);
            //txtView.setText("Loading database: " + prog2 + "%"); //very slow
            pgsBar.setProgress((int)percentLong);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //mDialog.dismiss();
            LinearLayout l = (LinearLayout) findViewById(R.id.progressContainer);
            l.setVisibility(View.GONE);
            FrameLayout l2 = (FrameLayout) findViewById(R.id.hideduringcopy);
            l2.setVisibility(View.VISIBLE);
        }
    }

    public void openSettings(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SettingsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = "practice";//editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
/*
        //testing
        SharedPreferences.Editor editor1 = sharedPref.edit();
        editor1.putString("PHTheme","PHDark");
        editor1.commit();
        */
        localSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals("PHTheme")) {
                recreate();
            }
        };
    };

        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);

        // Install databases if necessary.
        //File database = getDatabasePath("philolog_us.db");
        //if (!database.exists()) {
            new LoadDatabaseTask(this).execute(this, null, null);
            //PHDBHandler.getInstance(this).getReadableDatabase();
        //}

        if (findViewById(R.id.word_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            //Log.e("abc", "two pane for real: " + mTwoPane);

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((WordListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.word_list)).setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
        /*
        Keyboard mKeyboard= new Keyboard(getContext(), R.xml.phkeyboardgreek);
        mKeyboardView = (PHKeyboardView)view.findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard( mKeyboard );
        // Do not show the preview balloons
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(new PHLocalOnKeyboardActionListener((EditText)e, mKeyboardView, getContext()));

        //mKeyboardView.setLang(lang);

        //http://debugreport.blogspot.com/2012/09/how-to-hide-android-soft-keyboard.html
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        */
    }
    public void clearSearch(View v)
    {
        EditText s = findViewById(R.id.word_search);
        s.setText("");
    }
    /**
     * Callback method from {@link WordListFragment.Callbacks} indicating that
     * the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(long id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(WordDetailFragment.ARG_ITEM_ID, id);
            WordDetailFragment fragment = new WordDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.word_detail_container, fragment).commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, WordDetailActivity.class);
            detailIntent.putExtra(WordDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            MenuInflater inflater = getMenuInflater();
            //inflater.inflate(R.menu.list_activity, menu);
            inflater.inflate(R.menu.options_menu, menu);

            return true;
        }
        */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
		/*
		if (R.id.newWord == item.getItemId()) {
			result = true;

			Word p = new Word();
			DatabaseHandler.getInstance(this).putWord(p);
			// Open a new fragment with the new id
			onItemSelected(p.id);
		}
		*/
        return result;
    }
}
