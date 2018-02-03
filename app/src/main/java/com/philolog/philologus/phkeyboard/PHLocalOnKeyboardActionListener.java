package com.philolog.philologus.phkeyboard;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.inputmethodservice.KeyboardView;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.util.Log;
import android.inputmethodservice.Keyboard;

import com.philolog.philologus.R;

/**
 * Created by jeremy on 5/13/17.
 */

public class PHLocalOnKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {
    public final static int CodeDelete   = -5; // Keyboard.KEYCODE_DELETE
    public final static int CodeCancel   = -3; // Keyboard.KEYCODE_CANCEL
    public final static int CodePrev     = 55000;
    public final static int CodeAllLeft  = 55001;
    public final static int CodeLeft     = 55002;
    public final static int CodeRight    = 55003;
    public final static int CodeAllRight = 55004;
    public final static int CodeNext     = 55005;
    public final static int CodeClear    = 55006;

    public final static int NO_ACCENT = 0;
    public final static int ACUTE = 1;
    public final static int CIRCUMFLEX = 2;
    public final static int GRAVE = 3;
    public final static int MACRON = 4;
    public final static int ROUGH_BREATHING = 5;
    public final static int SMOOTH_BREATHING =6;
    public final static int IOTA_SUBSCRIPT = 7;
    public final static int SURROUNDING_PARENTHESES = 8;
    public final static int DIAERESIS = 9;
    public final static int BREVE = 10;

    public final static int COMBINING_GRAVE             = 0x0300;
    public final static int COMBINING_ACUTE             = 0x0301;
    public final static int COMBINING_CIRCUMFLEX        = 0x0302;
    public final static int COMBINING_MACRON            = 0x0304;
    public final static int COMBINING_DIAERESIS         = 0x0308;
    public final static int COMBINING_SMOOTH_BREATHING  = 0x0313;
    public final static int COMBINING_ROUGH_BREATHING   = 0x0314;
    public final static int COMBINING_IOTA_SUBSCRIPT    = 0x0345;
    public final static int COMBINING_BREVE             = 0x0306;

    public EditText e;
    public PHKeyboardView kv;
    public Context c;

    private boolean caps = false;
    private String capsL = "l";
    private String miscL = "n"; //m for misc, n for normal

    public PHLocalOnKeyboardActionListener(EditText et, PHKeyboardView kview, Context co)
    {
        e = et;
        kv = kview;
        c = co;
    }

    @Override public void onKey(int primaryCode, int[] keyCodes) {

        Editable editable = e.getText();
        int start = e.getSelectionStart();

        // this will prevent inserting a char between combining accents
        String str2 = editable.toString();
        start = fixCursorStart(start, str2, e);
        String s = "";
        if( primaryCode == CodeCancel ) {
            //hideCustomKeyboard();

        } /* else if( primaryCode==CodeDelete ) {
                if( editable!=null && start>0 ) editable.delete(start - 1, start);
            } else if( primaryCode==CodeClear ) {
                if( editable!=null ) editable.clear();
            } else if( primaryCode==CodeLeft ) {
                if( start>0 ) edittext.setSelection(start - 1);
            } else if( primaryCode==CodeRight ) {
                if (start < edittext.length()) edittext.setSelection(start + 1);
            } else if( primaryCode==CodeAllLeft ) {
                edittext.setSelection(0);
            } else if( primaryCode==CodeAllRight ) {
                edittext.setSelection(edittext.length());
            } else if( primaryCode==CodePrev ) {
                View focusNew= edittext.focusSearch(View.FOCUS_BACKWARD);
                if( focusNew!=null ) focusNew.requestFocus();
            } else if( primaryCode==CodeNext ) {
                View focusNew= edittext.focusSearch(View.FOCUS_FORWARD);
                if( focusNew!=null ) focusNew.requestFocus();

            } */
        else if( primaryCode == 1 ) {
            s = "α";
        }  else if( primaryCode == 2 ) {
            s = "β";
        }  else if( primaryCode == 3 ) {
            s = "γ";
        }  else if( primaryCode == 4 ) {
            s = "δ";
        }  else if( primaryCode == 5 ) {
            s = "ε";
        }  else if( primaryCode == 6 ) {
            s = "ζ";
        }  else if( primaryCode == 7 ) {
            s = "η";
        }  else if( primaryCode == 8 ) {
            s = "θ";
        }  else if( primaryCode == 9 ) {
            s = "ι";
        }  else if( primaryCode == 10 ) {
            s = "κ";
        }  else if( primaryCode == 11) {
            s = "λ";
        }  else if( primaryCode == 12 ) {
            s = "μ";
        }  else if( primaryCode == 13 ) {
            s = "ν";
        }  else if( primaryCode == 14 ) {
            s = "ξ";
        }  else if( primaryCode == 15 ) {
            s = "ο";
        }  else if( primaryCode == 16 ) {
            s = "π";
        }  else if( primaryCode == 17 ) {
            s = "ρ";
        }  else if( primaryCode == 18 ) {
            s = "σ";
        }  else if( primaryCode == 19 ) {
            s = "τ";
        }  else if( primaryCode == 20 ) {
            s = "υ";
        }  else if( primaryCode == 21 ) {
            s = "φ";
        }  else if( primaryCode == 22 ) {
            s = "χ";
        }  else if( primaryCode == 23 ) {
            s = "ψ";
        }  else if( primaryCode == 24 ) {
            s = "ω";
        } else if( primaryCode == 40 ) {
            s = "a";
        }  else if( primaryCode == 41 ) {
            s = "b";
        }  else if( primaryCode == 42 ) {
            s = "c";
        }  else if( primaryCode == 43 ) {
            s = "d";
        }  else if( primaryCode == 44 ) {
            s = "e";
        }  else if( primaryCode == 45 ) {
            s = "f";
        }  else if( primaryCode == 46 ) {
            s = "g";
        }  else if( primaryCode == 47 ) {
            s = "h";
        }  else if( primaryCode == 48 ) {
            s = "i";
        }  else if( primaryCode == 49 ) {
            s = "j";
        }  else if( primaryCode == 50) {
            s = "k";
        }  else if( primaryCode == 51 ) {
            s = "l";
        }  else if( primaryCode == 52 ) {
            s = "m";
        }  else if( primaryCode == 53 ) {
            s = "n";
        }  else if( primaryCode == 54 ) {
            s = "o";
        }  else if( primaryCode == 55 ) {
            s = "p";
        }  else if( primaryCode == 56 ) {
            s = "q";
        }  else if( primaryCode == 57 ) {
            s = "r";
        }  else if( primaryCode == 58 ) {
            s = "s";
        }  else if( primaryCode == 59 ) {
            s = "t";
        }  else if( primaryCode == 60 ) {
            s = "u";
        }  else if( primaryCode == 61 ) {
            s = "v";
        }  else if( primaryCode == 62 ) {
            s = "w";
        }  else if( primaryCode == 63 ) {
            s = "x";
        }
        else if( primaryCode == 64 ) {
            s = "y";
        }  else if( primaryCode == 65 ) {
            s = "z";
        }
        else if( primaryCode == 38 ) { //Delete
            if( start > 0 )
            {
                int i = 0;
                while (isCombiningCharacter(str2.charAt(start - i - 1)) && start - i - 1 > 0)
                    i++;
                editable.delete(start - i - 1, start);
            }
        }

        if (!s.equals("")) {
            editable.insert(start, s);
        }
    }

    @Override public void onPress(int arg0) {
        //this removes the yellow preview when key is pressed.
        kv.setPreviewEnabled(false);
    }

    @Override public void onRelease(int primaryCode) {
    }

    @Override public void onText(CharSequence text) {
    }

    @Override public void swipeDown() {
    }

    @Override public void swipeLeft() {
    }

    @Override public void swipeRight() {
    }

    @Override public void swipeUp() {
    }

    //see if there are one or more combining characters to the right of the cursor
    //if so move past them, so we don't insert a character between the combining characters
    //and their letter.
    public int fixCursorStart(int start, String s, EditText edittext)
    {
        if (start < s.length()) //doesn't matter if we're already at end of string
        {
            char charToRight = s.charAt(start);
            while (isCombiningCharacter(charToRight))
            {
                start++;
                edittext.setSelection(start);

                if (start + 1 <= s.length())
                {
                    charToRight = s.charAt(start);
                }
                else //we're at the end
                {
                    break;
                }
            }
            return start;
        }
        else
        {
            return start;
        }
    }

    public boolean isCombiningCharacter(char s)
    {
        //test this with a visible character: s == 0x03B2 ||
        if (s == COMBINING_GRAVE ||
                s == COMBINING_ACUTE ||
                s == COMBINING_CIRCUMFLEX ||
                s == COMBINING_MACRON ||
                s == COMBINING_DIAERESIS ||
                s == COMBINING_SMOOTH_BREATHING ||
                s == COMBINING_ROUGH_BREATHING ||
                s == COMBINING_IOTA_SUBSCRIPT ||
                s == COMBINING_BREVE)
            return true;
        else
            return false;
    }

}
