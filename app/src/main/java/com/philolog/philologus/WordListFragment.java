package com.philolog.philologus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.philolog.philologus.database.PHDBHandler;
import com.philolog.philologus.database.Word;
import com.philolog.philologus.database.WordProvider;
import com.philolog.philologus.phkeyboard.PHKeyboardView;
import com.philolog.philologus.phkeyboard.PHLocalOnKeyboardActionListener;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class WordListFragment extends ListFragment implements OnClickListener {
    public PHKeyboardView mKeyboardView;
    public ListAdapter gla;
    //public ListAdapter lla;
    public CursorLoader cc;
    int lang = 0;
    View mView;
    ListView mWordListView;
    Parcelable mWordListInstance;

    static class WordHolder {
        public TextView wordTextView;

        public WordHolder(View view) {
            wordTextView = view.findViewById(R.id.word);
        }
    }

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
        Button b = null;
        if (mView != null) {
            b = mView.findViewById(R.id.toggleButton);
        }

        if (newLanguage == Word.LANG_LATIN) {
            //Log.e("abc", "langl = " + lang);
            WordProvider.URI_WORDS = WordProvider.LATIN_URI_WORDS;

            Word.TABLE_NAME = Word.LATIN_TABLE_NAME;
            Word.DEF_TABLE_NAME = Word.LATIN_DEF_TABLE_NAME;
            WordProvider.LANG_MAX_ID = WordProvider.LATIN_MAX_ID;
            //Word.FIELDS = Word.LATIN_FIELDS;
            lang = Word.LANG_LATIN;
            if (b != null)
            {
                b.setText(R.string.latin_button);
            }
        }
        else {
            //Log.e("abc", "langg = " + lang);
            WordProvider.URI_WORDS = WordProvider.GREEK_URI_WORDS;
            WordProvider.LANG_MAX_ID = WordProvider.GREEK_MAX_ID;
            Word.TABLE_NAME = Word.GREEK_TABLE_NAME;
            Word.DEF_TABLE_NAME = Word.GREEK_DEF_TABLE_NAME;
            //Word.FIELDS = Word.GREEK_FIELDS;
            lang = Word.LANG_GREEK;
            if (b != null)
            {
                b.setText(R.string.greek_button);
            }
        }
        if (mKeyboardView != null) {
            mKeyboardView.setLang(newLanguage);
        }
    }

    /*
    this is done in the activity

    public void clearSearch(View v)
    {
        EditText s = v.findViewById(R.id.word_search);
        s.setText("");
    }
*/
    @Override
    public void onClick(View v) {

        EditText s = getView().findViewById(R.id.word_search);

        if (s != null) {
            s.setText("");
        }

        if (lang == Word.LANG_GREEK) {
            lang = Word.LANG_LATIN;
        }
        else
        {
            lang = Word.LANG_GREEK;
        }
        setLang(lang);

        cc.setUri(WordProvider.URI_WORDS);
        cc.forceLoad();

        SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences("PhilologusPref", 0); // 0 - for private mode
        SharedPreferences.Editor ed = pref.edit();
        ed.putInt("lang", lang);
        ed.apply(); //faster than commit()?
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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

        return view;
    }

    public void openKeyboard(View v)
    {
        if (mKeyboardView.getVisibility() == View.GONE) {

            Animation animation = AnimationUtils
                    .loadAnimation(getContext(),
                            R.anim.slide_in_bottom);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
            animation.setInterpolator(new LinearInterpolator());
            mKeyboardView.showWithAnimation(animation, null);

            mKeyboardView.setVisibility(View.VISIBLE);
            mKeyboardView.bringToFront();
            mKeyboardView.setEnabled(true);
            if ( v != null) {
                ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    public void hideCustomKeyboard(View v) {
        //mKeyboardView.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_bottom);
        mKeyboardView.startAnimation(animation);
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);

        //mKeyboardView.setVisibility(View.GONE);
        //mKeyboardView.invalidate();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
    }

    //https://stackoverflow.com/questions/32998439/saving-instance-of-fragments-listview
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        EditText e = (EditText) mView.findViewById(R.id.word_search);

        outState.putInt("lang", lang);
        outState.putString("wordPrefix", e.getText().toString());
        outState.putParcelable("WordListInstance", mWordListView.onSaveInstanceState());

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mWordListView = (ListView) getActivity().findViewById(android.R.id.list);
        EditText e = (EditText) mView.findViewById(R.id.word_search);

        if (savedInstanceState != null) {

            mActivatedPosition = savedInstanceState.getInt(STATE_ACTIVATED_POSITION);

            lang = savedInstanceState.getInt("lang");

            /*
            String wordPrefix = savedInstanceState.getString("wordPrefix");
            if (wordPrefix != null) {
                e.setText(wordPrefix.toString());
            }
            */

            setLang(lang);

            mWordListInstance = savedInstanceState.getParcelable("WordListInstance");
            mWordListView.onRestoreInstanceState(mWordListInstance);
        }

        WordListActivity wla = (WordListActivity) getActivity();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Select a parent for the keyboard. First search for a R.id.keyboardContainer viewgroup. If not found, use the rootWindow as parent
        ViewGroup keyboardContainer = null;
        ViewGroup parentViewGroup;
        if (wla.mTwoPane)
        {
            Log.e("abc", "two pane");
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            // Get the root view to add the keyboard subview
            ViewGroup rootView;

            if (currentapiVersion > Build.VERSION_CODES.KITKAT) {
                // Workaround for devices with softkeys. We cant not use  getRootView() because the keyboard would be below the softkeys.
                rootView = (ViewGroup) getActivity().findViewById(android.R.id.content);
            } else {
                rootView = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();
            }

            // Create a dummy relative layout to align the keyboardView to the bottom
            ViewGroup relativeLayout = new RelativeLayout(getActivity());
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            rootView.addView(relativeLayout);

            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);     // Align to the bottom of the relativelayout
            parentViewGroup = relativeLayout;

            mKeyboardView = (PHKeyboardView) new PHKeyboardView(getContext(), null);
            mKeyboardView.setLayoutParams(params);
            mKeyboardView.setFocusable(true);
            mKeyboardView.setFocusableInTouchMode(true);
            mKeyboardView.setVisibility(View.GONE);

            parentViewGroup.addView(mKeyboardView);
        }
        else
        {
            Log.e("abc", "one pane");
            mKeyboardView = (PHKeyboardView)mView.findViewById(R.id.keyboardview);
        }

        Keyboard mKeyboard= new Keyboard(getContext(), R.xml.phkeyboardgreek);
        mKeyboardView.setKeyboard( mKeyboard );
        // Do not show the preview balloons
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(new PHLocalOnKeyboardActionListener((EditText)e, mKeyboardView, getContext()));
        mKeyboardView.setLang(lang);

        if(getResources().getBoolean(R.bool.portrait_only)){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences("PhilologusPref", 0); // 0 - for private mode
        lang = pref.getInt("lang", 0);

        setLang(lang);

        gla = new PHSimpleCursorAdapter(getActivity(),
                R.layout.word_listitem, null, new String[]{
                Word.COL_WORD}, new int[]{R.id.word}, 0);

        setListAdapter(gla);

        // Load the content
        getLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                cc = new CursorLoader(getActivity(),
                        WordProvider.URI_WORDS, Word.FIELDS, null, null,
                        null);
                return cc;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
                ((SimpleCursorAdapter) getListAdapter()).swapCursor(c);

                //load finished, scroll to selected item

                if (WordProvider.selectedSeq > 1) {
                    int listHeight = 0;
                    int itemHeight = 0;

                    try {
                        listHeight = mWordListView.getMeasuredHeight();
                        itemHeight = mWordListView.getChildAt(0).getMeasuredHeight();
                    } catch (Exception ex) { }

                    mWordListView.setSelectionFromTop(WordProvider.selectedSeq, listHeight / 2 - (itemHeight*2));
                }
                else
                {
                    mWordListView.setSelectionFromTop(0, 0);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
            }
        });

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }

        mWordListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText e = (EditText) getView().findViewById(R.id.word_search);
                e.clearFocus();
                //e.setSelected(false);
                //hideCustomKeyboard(view);
/*
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }*/
                return false;
            }
        });

        //EditText e = (EditText) mView.findViewById(R.id.word_search);

        //https://stackoverflow.com/questions/13586354/android-hide-soft-keyboard-from-edittext-while-not-losing-cursor/13975236
        //https://stackoverflow.com/questions/12870577/disable-input-method-of-edittext-but-keep-cursor-blinking
        e.setInputType(InputType.TYPE_NULL); //this is needed to hide normal soft keyboard; must be called after view created.

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            e.setRawInputType(InputType.TYPE_CLASS_TEXT);
            e.setTextIsSelectable(true); //also needed, or android:textIsSelectable="true" in xml
        }

        e.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                EditText e = mView.findViewById(R.id.word_search);
                final String wordPrefix = e.getText().toString();

                //Log.e("abc", "ontextchanged: " + wordPrefix);
                Uri newuri;
                if (wordPrefix.isEmpty()) {
                    newuri = WordProvider.GREEK_URI_WORDS;
                }
                else
                {
                    newuri = Uri.parse(WordProvider.GREEK_WORD_BASE + wordPrefix);
                }
                cc.setUri(newuri);
                cc.forceLoad();
                //scroll to item in onLoadFinished
            }
        });
        //http://debugreport.blogspot.com/2012/09/how-to-hide-android-soft-keyboard.html
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(e.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        e.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (hasFocus) {
                    openKeyboard(view);
                    view.requestFocus();
                    EditText e = (EditText)view;
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    //e.setCursorVisible(true);
                    //e.setTextIsSelectable(true);
                } else {
                    hideCustomKeyboard(view);
                }
            }
        });

        /*
        e.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                openKeyboard(view);
                return true;
            }
        });
        */
        //https://stackoverflow.com/questions/13586354/android-hide-soft-keyboard-from-edittext-while-not-losing-cursor/13975236
        e.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
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
    public void onResume() {
        super.onResume();
        cc.forceLoad();
        //loadData(); // make sure data has been reloaded into adapter first
        // ONLY call this part once the data items have been loaded back into the adapter
        // for example, inside a success callback from the network
        if (mWordListInstance != null) {
            mWordListView.onRestoreInstanceState(mWordListInstance);
            mWordListInstance = null;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //cc = null;
        //gla = null;
        //loadData(); // make sure data has been reloaded into adapter first
        // ONLY call this part once the data items have been loaded back into the adapter
        // for example, inside a success callback from the network
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

