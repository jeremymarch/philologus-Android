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
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.philolog.philologus.database.Word;
import com.philolog.philologus.WordListFragment.WordHolder;

/**
 * Created by jeremy on 1/30/18.
 */

//NEXT STEPS - FIX ME
    //https://developer.android.com/reference/android/arch/paging/PositionalDataSource.html
    //https://medium.com/@husayn.hakeem/android-by-example-googles-recent-android-paging-library-pokedex-d9ec1d4986e9
    //https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView
    //https://stackoverflow.com/questions/39825125/android-recyclerview-cursorloader-contentprovider-load-more
    //http://andraskindler.com/blog/2014/migrating-to-recyclerview-from-listview/
    //https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView
    //https://gist.github.com/nesquena/d09dc68ff07e845cc622
    //https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView

public class PHSimpleCursorAdapter extends SimpleCursorAdapter
{
    final Typeface mCustomFont;
    public static int pageSize = 1000;

    //https://stackoverflow.com/questions/4567969/viewholder-pattern-correctly-implemented-in-custom-cursoradapter

    public PHSimpleCursorAdapter(final Context context, final int layout, final Cursor c, final String[] from,
                                 final int[] to) {
        super(context, layout, c, from, to, 0);

        mCustomFont = Typeface.createFromAsset(context.getAssets(), "fonts/newathu5.ttf");

        this.setViewBinder((view, cursor, columnIndex) -> {
                String word = cursor.getString(columnIndex);
                TextView textView = (TextView) view;
                //strip numbers from string
                textView.setText(word.replaceAll("\\d", ""));
                return true;

//                if (aColumnIndex == 1 && Word.TABLE_NAME.equals(Word.LATIN_TABLE_NAME)) {
//                    String word = aCursor.getString(aColumnIndex);
//                    TextView textView = (TextView) aView;
//                    textView.setText(removeMacronBreve(word));
//                    return true;
//                }

            //return false;
        });
    }

//    private String removeMacronBreve(String word)
//    {
//        word = word.replaceAll("ā", "a");
//        word = word.replaceAll("ē", "e");
//        word = word.replaceAll("ī", "i");
//        word = word.replaceAll("ō", "o");
//        word = word.replaceAll("ū", "u");
//
//        word = word.replaceAll("ă", "a");
//        word = word.replaceAll("ĕ", "e");
//        word = word.replaceAll("ĭ", "i");
//        word = word.replaceAll("ŏ", "o");
//        word = word.replaceAll("ŭ", "u");
//
//        word = word.replaceAll("Ā", "A");
//        word = word.replaceAll("Ē", "E");
//        word = word.replaceAll("Ī", "I");
//        word = word.replaceAll("Ō", "O");
//        word = word.replaceAll("Ū", "U");
//
//        word = word.replaceAll("Ă", "A");
//        word = word.replaceAll("Ĕ", "E");
//        word = word.replaceAll("Ĭ", "I");
//        word = word.replaceAll("Ŏ", "O");
//        word = word.replaceAll("Ŭ", "U");
//
//        return word;
//    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //View view = LayoutInflater.from(context).inflate(
        //        R.layout.word_listitem, parent, false);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(
                R.layout.word_listitem, parent, false);
        WordListFragment.WordHolder viewHolder = new WordHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        super.bindView(view, context, cursor);

        WordHolder viewHolder = (WordHolder) view.getTag();
        if (Word.TABLE_NAME.equals(Word.GREEK_TABLE_NAME)) {
            viewHolder.wordTextView.setTypeface(mCustomFont);
            viewHolder.wordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,28.0F);
            //viewHolder.wordTextView.setText(cursor.getString(1));
        }
        else
        {
            viewHolder.wordTextView.setTypeface(Typeface.SANS_SERIF);
            viewHolder.wordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,26.0F);
            //viewHolder.wordTextView.setText(cursor.getString(1));
        }
    }
}
