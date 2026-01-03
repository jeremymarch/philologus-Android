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
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.philolog.philologus.database.Word;

public class PHSimpleCursorAdapter extends SimpleCursorAdapter
{
    final Typeface mCustomFont;
    private final ColorStateList mTextColorStateList;

    public PHSimpleCursorAdapter(final Context context, final int layout, final Cursor c, final String[] from,
                                 final int[] to) {
        super(context, layout, c, from, to, 0);

        mCustomFont = Typeface.createFromAsset(context.getAssets(), "fonts/newathu5.ttf");
        mTextColorStateList = context.getResources().getColorStateList(R.color.selected_text_color, context.getTheme());

        this.setViewBinder((view, cursor, columnIndex) -> {
            String word = cursor.getString(columnIndex);
            TextView textView = (TextView) view;
            textView.setText(word.replaceAll("\\d", ""));
            return true;
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.word_listitem, parent, false);
        WordListFragment.WordHolder viewHolder = new WordListFragment.WordHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        super.bindView(view, context, cursor);

        WordListFragment.WordHolder viewHolder = (WordListFragment.WordHolder) view.getTag();
        viewHolder.wordTextView.setTextColor(mTextColorStateList);

        if (Word.TABLE_NAME.equals(Word.GREEK_TABLE_NAME)) {
            viewHolder.wordTextView.setTypeface(mCustomFont);
            viewHolder.wordTextView.setTextSize(28);
        }
        else
        {
            viewHolder.wordTextView.setTypeface(Typeface.SANS_SERIF);
            viewHolder.wordTextView.setTextSize(26);
        }
    }
}
