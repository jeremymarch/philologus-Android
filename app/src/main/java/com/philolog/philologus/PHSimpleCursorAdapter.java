package com.philolog.philologus;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philolog.philologus.database.Word;

/**
 * Created by jeremy on 1/30/18.
 */

public class PHSimpleCursorAdapter extends android.support.v4.widget.SimpleCursorAdapter
{
    private Typeface mCustomFont;
    private Context mContext;
    public static int pageSize = 1000;
    //private TextView mTextView;

    public PHSimpleCursorAdapter(final Context context, final int layout, final Cursor c, final String[] from,
                                 final int[] to, final int flags) {
        super(context, layout, c, from, to, flags);
        //mContext = context;
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
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.word_listitem, parent);
        if (view == null)
        {
            return null;
        }
        if (position == 1000) {
            // set your color
            view.setBackgroundColor(Color.BLUE);
        }
        else
        {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }
*/
}
