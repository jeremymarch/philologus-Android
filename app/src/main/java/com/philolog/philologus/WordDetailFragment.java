package com.philolog.philologus;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.philolog.philologus.database.PHDBHandler;
import com.philolog.philologus.database.Word;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (def != "") {
            definitionView = ((WebView) rootView.findViewById(R.id.definition));
            String html = "<html><head><style> " +
                    ".l1 { margin-left: 18px;position:relative;text-indent:-18px; } " +
                    ".l2 { margin-left: 18px;position:relative;text-indent:-18px; } " +
                    ".l3 { margin-left: 18px;position:relative;text-indent:-18px; } " +
                    ".l4 { margin-left: 18px;position:relative;text-indent:-18px; } " +
                    ".l5 { margin-left: 18px;position:relative;text-indent:-18px; } " +
                    "@font-face {" +
                    "font-family: 'newathu5'; " +
                    "src: url('fonts/newathu5.ttf'); } " +
                    ".body {font-family: 'newathu5';line-height:1.2;margin:8px 8px;font-size:14pt;} " +
                    ".fo {color:blue;} " +
                    ".qu {color:blue;} " +
                    ".qu:before { content: '\"'; } " +
                    ".qu:after { content: '\"'; }  " +
                    ".tr {font-weight:bold;} " +
                    ".au {color:red;} " +
                    ".bi {color:green;} " +
                    ".ti {color:orange;} " +
                    ".label {font-weight:bold;padding-right:0px;text-indent:0px;} " +
                    ".label:after { content: ' '; } " +
                    ".orth {font-weight:bold; } " +
                    "</style></head><BODY>" +
                    def + "</body></html>";
            definitionView.loadDataWithBaseURL("file:///android_asset/",html, "text/html", "UTF-8", "");
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
