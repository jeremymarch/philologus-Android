package com.philolog.philologus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.philolog.philologus.database.PHDBHandler;
import com.philolog.philologus.database.Word;
import com.philolog.philologus.database.WordProvider;

public class WordListFragment extends ListFragment implements OnClickListener {

    public ListAdapter gla;
    //public ListAdapter lla;
    public CursorLoader cc;
    int lang = 0;
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(long l);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(long id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WordListFragment() {
    }

    private void setLang(int newLanguage)
    {
        if (newLanguage == Word.LANG_LATIN) {
            //Log.e("abc", "langl = " + lang);
            WordProvider.URI_WORDS = WordProvider.LATIN_URI_WORDS;

            Word.TABLE_NAME = Word.LATIN_TABLE_NAME;
            Word.DEF_TABLE_NAME = Word.LATIN_DEF_TABLE_NAME;
            //Word.FIELDS = Word.LATIN_FIELDS;
            lang = Word.LANG_LATIN;
        }
        else {
            //Log.e("abc", "langg = " + lang);
            WordProvider.URI_WORDS = WordProvider.GREEK_URI_WORDS;

            Word.TABLE_NAME = Word.GREEK_TABLE_NAME;
            Word.DEF_TABLE_NAME = Word.GREEK_DEF_TABLE_NAME;
            //Word.FIELDS = Word.GREEK_FIELDS;
            lang = Word.LANG_GREEK;
        }
    }

    @Override
    public void onClick(View v) {
        Button b = v.findViewById(R.id.toggleButton);
        if (lang == Word.LANG_GREEK) {
            lang = Word.LANG_LATIN;
            b.setText(R.string.latin_button);
        }
        else
        {
            lang = Word.LANG_GREEK;
            b.setText(R.string.greek_button);
        }
        setLang(lang);

        //cc.setProjection(Word.FIELDS);
        cc.setUri(WordProvider.URI_WORDS);
        cc.forceLoad();

        SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences("PhilologusPref", 0); // 0 - for private mode
        SharedPreferences.Editor ed = pref.edit();
        ed.putInt("lang", lang);
        ed.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences("PhilologusPref", 0); // 0 - for private mode
        lang = pref.getInt("lang", 0);

        setLang(lang);

        gla = new PHSimpleCursorAdapter(getActivity(),
                R.layout.word_listitem, null, new String[]{
                Word.COL_WORD}, new int[]{R.id.word}, 0);

        /*lla = new PHSimpleCursorAdapter(getActivity(),
                R.layout.word_listitem, null, new String[]{
                Word.COL_WORD}, new int[]{R.id.word}, 0);
*/
        setListAdapter(gla);

        cc = new CursorLoader(getActivity(),
                WordProvider.URI_WORDS, Word.FIELDS, null, null,
                null);

        // Load the content
        getLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return cc;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
                ((SimpleCursorAdapter) getListAdapter()).swapCursor(c);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_word_list, null);
        Button b = view.findViewById(R.id.toggleButton);
        if (lang == Word.LANG_GREEK)
        {
            b.setText(R.string.greek_button);
        }
        else
        {
            b.setText(R.string.latin_button);
        }
        b.setOnClickListener(this);

        EditText e = (EditText) view.findViewById(R.id.word_search);

        e.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                EditText e = (EditText) view.findViewById(R.id.word_search);
                String wordPrefix = e.getText().toString();
                int seq = PHDBHandler.getInstance(getContext()).scrollTo(wordPrefix);
                if (seq > -1) {
                    getListView().setSelection(seq - 1);// .smoothScrollToPosition(5000);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }

        getListView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //EditText e = (EditText) v.findViewById(R.id.word_search);
                //e.setSelected(false);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
                                long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(getListAdapter().getItemId(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}

