package com.philolog.philologus;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.philolog.philologus.database.Word;
import com.philolog.philologus.WordListFragment.WordHolder;

/**
 * Created by jeremy on 1/30/18.
 */

public class PHSimpleCursorAdapter extends android.support.v4.widget.SimpleCursorAdapter
{
    private Typeface mCustomFont;
    private Context mContext;
    public static int pageSize = 1000;
    //private TextView mTextView;

    //https://stackoverflow.com/questions/4567969/viewholder-pattern-correctly-implemented-in-custom-cursoradapter

    public PHSimpleCursorAdapter(final Context context, final int layout, final Cursor c, final String[] from,
                                 final int[] to, final int flags) {
        super(context, layout, c, from, to, flags);

        mCustomFont = Typeface.createFromAsset(context.getAssets(), "fonts/newathu5.ttf");

        this.setViewBinder(new ViewBinder() {

            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

                if (aColumnIndex == 1 && Word.TABLE_NAME.equals(Word.LATIN_TABLE_NAME)) {
                    String word = aCursor.getString(aColumnIndex);
                    TextView textView = (TextView) aView;
                    textView.setText(removeMacronBreve(word));
                    return true;
                }

                return false;
            }
        });
    }

    private String removeMacronBreve(String word)
    {
        word = word.replaceAll("ā", "a");
        word = word.replaceAll("ē", "e");
        word = word.replaceAll("ī", "i");
        word = word.replaceAll("ō", "o");
        word = word.replaceAll("ū", "u");

        word = word.replaceAll("ă", "a");
        word = word.replaceAll("ĕ", "e");
        word = word.replaceAll("ĭ", "i");
        word = word.replaceAll("ŏ", "o");
        word = word.replaceAll("ŭ", "u");

        word = word.replaceAll("Ā", "A");
        word = word.replaceAll("Ē", "E");
        word = word.replaceAll("Ī", "I");
        word = word.replaceAll("Ō", "O");
        word = word.replaceAll("Ū", "U");

        word = word.replaceAll("Ă", "A");
        word = word.replaceAll("Ĕ", "E");
        word = word.replaceAll("Ĭ", "I");
        word = word.replaceAll("Ŏ", "O");
        word = word.replaceAll("Ŭ", "U");

        return word;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
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
            viewHolder.wordTextView.setTextSize(23);
            //viewHolder.wordTextView.setText(cursor.getString(1));
        }
        else
        {
            viewHolder.wordTextView.setTypeface(Typeface.SANS_SERIF);
            viewHolder.wordTextView.setTextSize(21);
            //viewHolder.wordTextView.setText(cursor.getString(1));
        }
    }
}

