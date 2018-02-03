package com.philolog.philologus;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public class WordListActivity extends FragmentActivity implements
        WordListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        if (findViewById(R.id.word_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((WordListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.word_list)).setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
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
