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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.philolog.philologus.database.Word;
import com.philolog.philologus.database.WordProvider;
import com.philolog.philologus.phkeyboard.PHKeyboardView;
import com.philolog.philologus.phkeyboard.PHLocalOnKeyboardActionListener;

import java.util.Objects;

public class WordListFragment extends ListFragment implements View.OnClickListener, LoaderCallbacks<Cursor> {
    public PHKeyboardView mKeyboardView;
    public ListAdapter gla;
    public CursorLoader cc;
    int lang = 0;
    View mView;
    ListView mWordListView;
    Parcelable mWordListInstance;
    private EditText search_textbox;
    private boolean isTwoPane;
    private int mKeyboardHeight;

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
        void onItemSelected(long l);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    final static Callbacks sDummyCallbacks = id -> {
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
            WordProvider.URI_WORDS = WordProvider.LATIN_URI_WORDS;

            Word.TABLE_NAME = Word.LATIN_TABLE_NAME;
            Word.DEF_TABLE_NAME = Word.LATIN_DEF_TABLE_NAME;
            WordProvider.LANG_MAX_ID = WordProvider.LATIN_MAX_ID;
            lang = Word.LANG_LATIN;
            if (b != null)
            {
                b.setText(R.string.latin_button);
            }
        }
        else {
            WordProvider.URI_WORDS = WordProvider.GREEK_URI_WORDS;
            WordProvider.LANG_MAX_ID = WordProvider.GREEK_MAX_ID;
            Word.TABLE_NAME = Word.GREEK_TABLE_NAME;
            Word.DEF_TABLE_NAME = Word.GREEK_DEF_TABLE_NAME;
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

    @Override
    public void onClick(View v) {
        View view = getView();
        if (view != null) {
            EditText search_textbox = view.findViewById(R.id.word_search);
            if (search_textbox != null) {
                search_textbox.setText("");
            }
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

        Context context = getContext();
        if (context != null) {
            SharedPreferences pref = context.getApplicationContext().getSharedPreferences("PhilologusPref", 0);
            SharedPreferences.Editor ed = pref.edit();
            ed.putInt("lang", lang);
            ed.apply();
        }
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

        search_textbox = view.findViewById(R.id.word_search);
        search_textbox.setShowSoftInputOnFocus(false);
        search_textbox.setTextSize(TypedValue.COMPLEX_UNIT_SP,26.0F);

        Button b = view.findViewById(R.id.toggleButton);
        if (lang == Word.LANG_GREEK) {
            b.setText(R.string.greek_button);
        }
        else {
            b.setText(R.string.latin_button);
        }
        b.setOnClickListener(this);

        return view;
    }

    public void openKeyboard(View v)
    {
        if (mKeyboardView.getVisibility() == View.GONE) {
            if (isTwoPane) {
                mWordListView.setPadding(0, 0, 0, mKeyboardHeight);
            }
            Animation animation = AnimationUtils
                    .loadAnimation(getContext(),
                            R.anim.slide_in_bottom);
            mKeyboardView.setVisibility(View.VISIBLE);
            mKeyboardView.bringToFront();
            mKeyboardView.setEnabled(true);
            if ( v != null) {
                ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    public void hideCustomKeyboard(View v) {
        if (isTwoPane) {
            mWordListView.setPadding(0, 0, 0, 0);
        }
        if (mKeyboardView.getVisibility() == View.VISIBLE) {
            Context context = getContext();
            if (context != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom);
                mKeyboardView.startAnimation(animation);
                mKeyboardView.setVisibility(View.GONE);
                mKeyboardView.setEnabled(false);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;

        mWordListView = getListView();

        if (savedInstanceState != null) {

            mActivatedPosition = savedInstanceState.getInt(STATE_ACTIVATED_POSITION);

            lang = savedInstanceState.getInt("lang");

            setLang(lang);

            mWordListInstance = savedInstanceState.getParcelable("WordListInstance");
            mWordListView.onRestoreInstanceState(mWordListInstance);
        }

        if(getResources().getBoolean(R.bool.portrait_only)){
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Context context = getContext();
        if (context != null) {
            SharedPreferences pref = context.getApplicationContext().getSharedPreferences("PhilologusPref", 0);
            lang = pref.getInt("lang", 0);
        }

        setLang(lang);
        gla = new PHSimpleCursorAdapter(requireActivity(),
                R.layout.word_listitem, null, new String[]{
                Word.COL_WORD}, new int[]{R.id.word});

        setListAdapter(gla);

        LoaderManager.getInstance(this).initLoader(0, null, this);

        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }

        mWordListView.setOnTouchListener((v, event) -> {
            search_textbox.clearFocus();
            return false;
        });

        search_textbox.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final String wordPrefix = s.toString();

                Uri newuri;
                if (wordPrefix.isEmpty()) {
                    newuri = WordProvider.URI_WORDS;
                }
                else
                {
                    newuri = Uri.parse(WordProvider.GREEK_WORD_BASE + wordPrefix);
                }
                cc.setUri(newuri);
                cc.forceLoad();
            }
        });
    }

    public void onTwoPaneChanged(boolean isTwoPane) {
        this.isTwoPane = isTwoPane;

        if (isTwoPane) {
            FrameLayout rootView = requireActivity().findViewById(android.R.id.content);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            );
            mKeyboardView = new PHKeyboardView(getContext(), null);
            mKeyboardView.setLayoutParams(params);
            rootView.addView(mKeyboardView);

        } else {
            mKeyboardView = requireView().findViewById(R.id.keyboardview);
        }

        mKeyboardView.setFocusable(true);
        mKeyboardView.setFocusableInTouchMode(true);
        mKeyboardView.setVisibility(View.GONE);

        Keyboard mKeyboard = new Keyboard(getContext(), R.xml.phkeyboardgreek);
        mKeyboardHeight = mKeyboard.getHeight();
        mKeyboardView.setKeyboard( mKeyboard );
        mKeyboardView.setOnKeyboardActionListener(new PHLocalOnKeyboardActionListener(search_textbox, mKeyboardView, getContext()));
        mKeyboardView.setLang(lang);

        search_textbox.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                openKeyboard(v);
            } else {
                hideCustomKeyboard(v);
            }
        });

        search_textbox.setOnClickListener(v -> openKeyboard(v));

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lang", lang);
        outState.putString("wordPrefix", search_textbox.getText().toString());
        outState.putParcelable("WordListInstance", mWordListView.onSaveInstanceState());

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        cc.forceLoad();
        if (mWordListInstance != null) {
            mWordListView.onRestoreInstanceState(mWordListInstance);
            mWordListInstance = null;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(@NonNull ListView listView, @NonNull View view, int position,
                                long id) {
        super.onListItemClick(listView, view, position, id);
        ListAdapter list = getListAdapter();
        if (list != null) {
            mCallbacks.onItemSelected(list.getItemId(position));
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
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

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        cc = new CursorLoader(Objects.requireNonNull(requireActivity()),
                WordProvider.URI_WORDS, Word.FIELDS, null, null,
                null);
        return cc;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor c) {
        SimpleCursorAdapter cursor = (SimpleCursorAdapter) getListAdapter();
        if (cursor != null) {
            cursor.swapCursor(c);
        }

        if (WordProvider.selectedSeq > 1) {
            int listHeight = 0;
            int itemHeight = 0;

            try {
                listHeight = mWordListView.getMeasuredHeight();
                itemHeight = mWordListView.getChildAt(0).getMeasuredHeight();
            } catch (Exception ex) {
                Log.e("jwm", "exception getting listHeight");
            }

            mWordListView.setItemChecked(WordProvider.selectedSeq - 1, true);
            mWordListView.setSelectionFromTop(WordProvider.selectedSeq, listHeight / 2 - (itemHeight * 2));

        }
        else
        {
            mWordListView.clearChoices();
            mWordListView.setSelectionFromTop(0, 0);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> arg0) {
        SimpleCursorAdapter cursor = (SimpleCursorAdapter) getListAdapter();
        if (cursor != null) {
            cursor.swapCursor(null);
        }
    }
}
