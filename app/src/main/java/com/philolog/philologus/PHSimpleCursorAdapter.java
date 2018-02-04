package com.philolog.philologus;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.philolog.philologus.database.Word;

/**
 * Created by jeremy on 1/30/18.
 */

public class PHSimpleCursorAdapter extends android.support.v4.widget.SimpleCursorAdapter
{
    private Typeface mCustomFont;
    //private TextView mTextView;

    public PHSimpleCursorAdapter(final Context context, final int layout, final Cursor c, final String[] from,
                                 final int[] to, final int flags) {
        super(context, layout, c, from, to, flags);
        mCustomFont = Typeface.createFromAsset(context.getAssets(), "fonts/newathu5.ttf");
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        super.bindView(view, context, cursor);

        final TextView _TextViewTitle = (TextView) view.findViewById(R.id.word);
        if (Word.TABLE_NAME == Word.GREEK_TABLE_NAME) {
            _TextViewTitle.setTypeface(mCustomFont);
        }
        else
        {
            _TextViewTitle.setTypeface(Typeface.SANS_SERIF);
        }
    }
}
