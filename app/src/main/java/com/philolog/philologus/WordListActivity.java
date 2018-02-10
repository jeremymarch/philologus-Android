package com.philolog.philologus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.philolog.philologus.database.PHDBHandler;
import com.philolog.philologus.phkeyboard.PHKeyboardView;
import com.philolog.philologus.phkeyboard.PHLocalOnKeyboardActionListener;

import java.io.File;

public class WordListActivity extends FragmentActivity implements
        WordListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    //public PHKeyboardView mKeyboardView;
    public boolean mTwoPane;

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //getFragmentManager().putFragment(outState,"myfragment",myfragment);
        outState.putBoolean("twoPane", mTwoPane);
    }
    public void onRestoreInstanceState(Bundle inState){
        //myFragment = getFragmentManager().getFragment(inState,"myfragment");
        mTwoPane = inState.getBoolean("twoPane");
    }

    //used to show a message while copying over database on first load
    private class LoadDatabaseTask extends AsyncTask<Context, Void, Void> {
        Context mContext;
        ProgressDialog mDialog;

        // Provide a constructor so we can get a Context to use to create
        // the ProgressDialog.
        public LoadDatabaseTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Loading database...");
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            // Copy database.
            PHDBHandler.getInstance(contexts[0]).getReadableDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        // Install databases if necessary.
        File database = getDatabasePath("philolog_us.db");
        if (!database.exists()) {
            new LoadDatabaseTask(this).execute(this, null, null);
        }

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
