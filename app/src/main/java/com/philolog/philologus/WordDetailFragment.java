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

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.philolog.philologus.database.PHDBHandler;
import com.philolog.philologus.database.Word;

import java.util.Arrays;

/**
 * A fragment representing a single Word detail screen.
 * This fragment is either contained in a {@link WordListActivity}
 * in two-pane mode (on tablets) or a {@link WordDetailActivity}
 * on handsets.
 */
public class WordDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Word mItem;
    private String def = "";
    /**
     * The UI elements showing the details of the Word
     */
    private WebView definitionView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WordDetailFragment() {
    }

    String themeName;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.phThemeName, typedValue, true);
        themeName = typedValue.string.toString();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Should use the contentprovider here ideally
            //mItem = PHDBHandler.getInstance(getActivity()).getWord(getArguments().getLong(ARG_ITEM_ID));
            //Log.e("abcdefid", "id: " + getArguments().getLong(ARG_ITEM_ID));

            def = PHDBHandler.getInstance(getContext()).getDef(getArguments().getLong(ARG_ITEM_ID));
        }
        if(getResources().getBoolean(R.bool.portrait_only)){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_word_detail, container, false);

        if (!def.equals("")) {
            definitionView = ((WebView) rootView.findViewById(R.id.definition));

            //SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences("PhilologusPref", 0); // 0 - for private mode
            //Boolean deepIndent = pref.getBoolean("PHMultiLevelIndent", true);

            String bg = "white";
            String text = "black";
            String author = "red";
            String foreign = "blue";
            String quote = "blue";
            String bibl = "green";
            String title = "orange";
            if (themeName.equals("PHDark")) {
                //fixes white flashing of webview
                definitionView.setBackgroundColor(Color.BLACK);

                bg = "black";
                text = "white";
                foreign = "#03a5fc";
                quote = "#03a5fc";
            }
            boolean trItalics = true; //false for bold
            int indentPx = 38;
            int minWidthForDeepIndent = 393;
            //pixel 3a width = 411
            //pixel 3XL width =
            String html = "<!DOCTYPE html>\n<html lang=\"en\"><head><title>philolog.us</title><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" /><style> " +
                    ".l1 { margin-left: " + (indentPx ) + "px;position:relative; } " +
                    ".l2 { margin-left: " + (indentPx ) + "px;position:relative; } " +
                    ".l3 { margin-left: " + (indentPx ) + "px;position:relative; } " +
                    ".l4 { margin-left: " + (indentPx ) + "px;position:relative; } " +
                    ".l5 { margin-left: " + (indentPx ) + "px;position:relative; } " +
                    "@media (min-width : " + minWidthForDeepIndent + "px) { " +
                    ".l1 { margin-left: " + (indentPx) + "px;position:relative; } " +
                    ".l2 { margin-left: " + (indentPx * 2) + "px;position:relative; } " +
                    ".l3 { margin-left: " + (indentPx * 3) + "px;position:relative; } " +
                    ".l4 { margin-left: " + (indentPx * 4) + "px;position:relative; } " +
                    ".l5 { margin-left: " + (indentPx * 5) + "px;position:relative; } " +
                    " } " +
                    ".label {font-weight:bold;padding-right:0px;position:absolute;left:-"+ indentPx + "px;} " +
                    //".label:after { content: ' '; } " +
                    "@font-face {font-family: 'newathu5'; src: url('fonts/newathu5.ttf'); } " +
                    "BODY {color:" + text + ";background-color:" + bg + "; margin-top:16px;} " +
                    ".body {font-family: 'newathu5';line-height:1.2;margin:8px 8px;font-size:14pt;} " +
                    ".fo {color:" + foreign + ";} " +
                    ".qu {color:" + quote + ";} " +
                    ".qu:before { content: '\"'; } " +
                    ".qu:after { content: '\"'; }  " +
                ((trItalics) ? ".tr {font-style:italic;} " : ".tr {font-weight:bold;} ") +
                    ".au {color:" + author + ";} " +
                    ".bi {color:" + bibl + ";} " +
                    ".ti {color:" + title + ";} " +
                    ".orth {font-weight:bold; } " +
                    "</style></head><BODY>" +
                    def + "</br></body></html>";
            //Log.e("jwm", html.substring(18500));
            //definitionView.setLayerType(View.LAYER_TYPE_HARDWARE, null); //this crashes on gero?
            definitionView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            definitionView.getSettings().setJavaScriptEnabled(false);
            definitionView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            definitionView.setScrollbarFadingEnabled(true);

            definitionView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        updateWordFromUI();
    }

    private void updateWordFromUI() {
        /*
    	if (mItem != null) {
    		mItem.word = word.getText().toString();

    		DatabaseHandler.getInstance(getActivity()).putWord(mItem);
        }
        */
    }
}
