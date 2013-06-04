/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * This activity presents the user with application usage instructions.
 */

public class HelpActivity extends Activity {

    // Define views.
    private TextView mTvHelpBody;

    /**
     * Implements onCreate().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // We are using the Theme.Dialog theme in this activity's entry in the
        // manifest, thus, all we need to do is inflate the layout.
        super.onCreate(savedInstanceState);

        // Set the activity to full screen mode.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_help);

        // Attach views to their corresponding resource ids.
        mTvHelpBody = (TextView) findViewById(R.id.tvHelpBody);

        // Parse the help string html.
        mTvHelpBody.setText(Html.fromHtml(getResources().getString(R.string.tv_help), null, null));
    }
}
