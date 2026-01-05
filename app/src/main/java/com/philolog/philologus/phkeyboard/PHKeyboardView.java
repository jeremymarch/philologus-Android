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
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import androidx.core.content.ContextCompat;

import com.philolog.philologus.R;
import com.philolog.philologus.database.Word;

import java.util.List;

public class PHKeyboardView extends KeyboardView {

    private final int keyTextColor;
    private final int keyTextColorDown;
    private final int keyboardBGColor;
    private final Paint mPaint;
    private final Drawable mKeyBackground;
    private final Drawable mKeyPressedBackground;
    private final Drawable mDeleteKeyBackground;
    private final Drawable mDeleteKeyPressedBackground;
    private final Drawable mDeleteIcon;
    private final Drawable mDeleteIconPressed;
    private final int mLabelFontSizePx;
    private final int mLabelOffsetPx;

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
        float scale = context.getResources().getDisplayMetrics().density;

        // Pre-calculate pixel values
        mLabelFontSizePx = (int) (23.0f * scale + 0.5f);
        mLabelOffsetPx = (int) (9 * scale + 0.5f);

        // Cache drawables
        mKeyBackground = ContextCompat.getDrawable(context, R.drawable.normalbutton);
        mKeyPressedBackground = ContextCompat.getDrawable(context, R.drawable.normalbuttondown);
        mDeleteKeyBackground = ContextCompat.getDrawable(context, R.drawable.greybutton);
        mDeleteKeyPressedBackground = ContextCompat.getDrawable(context, R.drawable.normalbuttondown);
        mDeleteIcon = ContextCompat.getDrawable(context, R.drawable.deleteicon);
        mDeleteIconPressed = ContextCompat.getDrawable(context, R.drawable.deleteicond);
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

        // Setup constant paint properties
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
        mPaint.setFakeBoldText(true);
        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.setTextSize(mLabelFontSizePx);

        for (Keyboard.Key key : keys) {
            // Draw key background
            Drawable dr;
            if (key.codes[0] == 38) { // Delete key
                dr = key.pressed ? mDeleteKeyPressedBackground : mDeleteKeyBackground;
            } else {
                dr = key.pressed ? mKeyPressedBackground : mKeyBackground;
            }
            if (dr != null) {
                dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                dr.draw(canvas);
            }

            // Set text color
            mPaint.setColor(key.pressed ? keyTextColorDown : keyTextColor);

            // Draw icon or label
            if (key.codes[0] == 38) { // Delete icon
                Drawable icon = key.pressed ? mDeleteIconPressed : mDeleteIcon;
                if (icon != null) {
                    double a = (Math.min(key.width, key.height)) * 0.66;
                    double y = key.y + ((key.height - a) / 2);
                    double x = key.x + ((key.width - a) / 2);
                    icon.setBounds((int) x, (int) y, (int) (x + a), (int) (y + a));
                    icon.draw(canvas);
                }
            } else if (key.label != null) { // Key label
                canvas.drawText(key.label, 0, key.label.length(),
                        key.x + ((float) key.width / 2),
                        key.y + ((float) key.height / 2) + mLabelOffsetPx,
                        mPaint);

            } else if (key.icon != null) { // Fallback for other icons
                key.icon.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                key.icon.draw(canvas);
            }
        }
    }
}
