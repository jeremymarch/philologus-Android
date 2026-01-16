package com.philolog.philologus.database;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    @Test
    public void getDefTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PHDBHandler dbHandler = PHDBHandler.getInstance(appContext);

        Word.TABLE_NAME = Word.GREEK_TABLE_NAME;

        long targetId = 1;
        String definition = dbHandler.getDef(targetId);
        
        // Assertions
        assertNotNull("Definition for ID " + targetId + " should not be null", definition);

        String expectedDef = "<div id=\"cross*a\" class=\"body\">α, <span class=\"orth\">ἄλφα</span> (q.v.), τό, indecl., <br/><br/><div class=\"l1\">first letter of the Gr. alphabet: as Numeral, αʹ = εἷς and <span class=\"fo\">πρῶτος,</span> but <span class=\"fo\">͵α</span> = 1,000.</div></div>";
        assertEquals("The definition text should match the expected value", expectedDef, definition);
        
        android.util.Log.d("DatabaseTest", "Found definition: " + definition);
    }
}
