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

package com.philolog.philologus.phkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Animation;
import androidx.core.content.ContextCompat;

import com.philolog.philologus.R;
import com.philolog.philologus.database.Word;

import java.util.List;

public class PHKeyboardView extends KeyboardView {

    public boolean mMFPressed = false;
    private final int keyTextColor;
    private final int keyTextColorDown;
    private final int keyboardBGColor;
    private final Paint mPaint;
    private final Typeface mGreekTypeface;
    private final float mScale;

    @SuppressWarnings("deprecation")
    public PHKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.phKeyTextColor, typedValue, true);
        keyTextColor = typedValue.data;
        theme.resolveAttribute(R.attr.phKeyTextColorDown, typedValue, true);
        keyTextColorDown = typedValue.data;
        theme.resolveAttribute(R.attr.phkeyboardBgColor, typedValue, true);
        keyboardBGColor = typedValue.data;

        mPaint = new Paint();
        mGreekTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/newathu5.ttf");
        mScale = context.getResources().getDisplayMetrics().density;
    }

    @SuppressWarnings("deprecation")
    public void setLang(int lang) {
        Keyboard keyboard;

        if (lang == Word.LANG_GREEK) {
            keyboard = new Keyboard(getContext(), R.xml.phkeyboardgreek);
        } else {
            keyboard = new Keyboard(getContext(), R.xml.phkeyboardlatin);
        }
        this.setKeyboard(keyboard);
        this.invalidateAllKeys();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final Keyboard keyboard = getKeyboard();
        if (keyboard == null) {
            return;
        }

        final List<Keyboard.Key> keys = keyboard.getKeys();
        if (keys == null || keys.isEmpty()) {
            return;
        }

        // Draw background
        mPaint.reset();
        mPaint.setColor(keyboardBGColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        final Context context = getContext();

        for (Keyboard.Key key : keys) {
            // Draw key background
            Drawable dr;
            if (key.codes[0] == 38) { // Delete key
                dr = ContextCompat.getDrawable(context, key.pressed ? R.drawable.normalbuttondown : R.drawable.greybutton);
            } else {
                dr = ContextCompat.getDrawable(context, key.pressed ? R.drawable.normalbuttondown : R.drawable.normalbutton);
            }
            if (dr != null) {
                dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                dr.draw(canvas);
            }

            // Setup paint for text/icon
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setAntiAlias(true);
            mPaint.setFakeBoldText(true);
            mPaint.setColor(key.pressed ? keyTextColorDown : keyTextColor);
            if (key.codes[0] == 39) {
                mPaint.setColor(Color.GRAY);
            }

            // Draw delete icon
            if (key.codes[0] == 38) {
                Drawable icon = ContextCompat.getDrawable(context, key.pressed ? R.drawable.deleteicond : R.drawable.deleteicon);
                if (icon != null) {
                    double a = (Math.min(key.width, key.height)) * 0.66;
                    double y = key.y + ((key.height - a) / 2);
                    double x = key.x + ((key.width - a) / 2);
                    icon.setBounds((int) x, (int) y, (int) (x + a), (int) (y + a));
                    icon.draw(canvas);
                }
            }
            // Draw key label
            else if (key.label != null) {
                float FONT_SIZE;
                // Determine font size and typeface
                if (key.codes[0] == 28 || key.codes[0] == 27 || key.codes[0] == 29 || key.codes[0] == 34) {
                    FONT_SIZE = (key.codes[0] == 29 || key.codes[0] == 34) ? 44.0f : 38.0f;
                    mPaint.setTypeface(mGreekTypeface);
                } else if (key.codes[0] == 32) {
                    FONT_SIZE = 23.0f;
                } else if (key.codes[0] == 33 && mMFPressed) {
                    FONT_SIZE = 32.0f;
                    mPaint.setTypeface(mGreekTypeface);
                } else {
                    FONT_SIZE = 23.0f;
                    mPaint.setTypeface(Typeface.DEFAULT);
                }

                final int fontSizeInPx = (int) (FONT_SIZE * mScale + 0.5f);
                mPaint.setTextSize(fontSizeInPx);

                // Determine label text and offset
                String s;
                int offset;
                if (key.codes[0] == 27) { s = "῾"; offset = 20; }
                else if (key.codes[0] == 28) { s = "᾿"; offset = 20; }
                else if (key.codes[0] == 29) { s = "´"; offset = 19; }
                else if (key.codes[0] == 34) { s = "`"; offset = 21; }
                else if (key.codes[0] == 30) { s = key.label.toString(); offset = 2; }
                else if (key.codes[0] == 33) { s = "—"; offset = 4; }
                else if (key.codes[0] == 32) { s = "ι"; offset = 14; }
                else if (key.codes[0] == 33 && mMFPressed) { s = ","; offset = 5; }
                else { s = key.label.toString(); offset = 9; }

                int finalOffset = (int) (offset * mScale + 0.5f);
                canvas.drawText(s, key.x + ((float) key.width / 2), key.y + ((float) key.height / 2) + finalOffset, mPaint);

            }
            // Fallback to draw other icons
            else if (key.icon != null) {
                key.icon.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                key.icon.draw(canvas);
            }
        }
    }

//    public int getUnicodeMode() {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
//        return Integer.parseInt(sharedPref.getString("UnicodeMode", "0"));
//    }

    public void showWithAnimation(Animation animation, final Runnable onComplete) {
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                postDelayed(onComplete, 200);
            }
        });

        setAnimation(animation);
    }
}
