package com.docker.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;

public class TermActivity extends AppCompatActivity {


    final private static String TAG = "TermActivity";
    private EditText mEntry;
    private EmulatorView mEmulatorView;
    private TermSession mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        mEntry = (EditText) findViewById(R.id.term_entry);
        mEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int action, KeyEvent ev) {
                // Ignore enter-key-up events
                if (ev != null && ev.getAction() == KeyEvent.ACTION_UP) {
                    return false;
                }
                // Don't try to send something if we're not connected yet
                TermSession session = mSession;
                if (mSession == null) {
                    return true;
                }

                Editable e = (Editable) v.getText();
                // Write to the terminal session
                session.write(e.toString());
                session.write('\r');
                TextKeyListener.clear(e);
                return true;
            }
        });

        /* Sends the content of the text entry box to the terminal, without
           sending a carriage return afterwards */
        Button sendButton = (Button) findViewById(R.id.term_entry_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Don't try to send something if we're not connected yet
                TermSession session = mSession;
                if (mSession == null) {
                    return;
                }
                Editable e = (Editable) mEntry.getText();
                session.write(e.toString());
                TextKeyListener.clear(e);
            }
        });

        /**
         * EmulatorView setup.
         */
        EmulatorView view = (EmulatorView) findViewById(R.id.emulatorView);
        mEmulatorView = view;

        /* Let the EmulatorView know the screen's density. */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.setDensity(metrics);

        /* Create a TermSession. */
        Intent myIntent = getIntent();
        TermSession session;
        // Create a local shell session.
        session = createLocalTermSession();
        if (session == null) {
            finish();
            return;
        }
        mSession = session;

        /* Attach the TermSession to the EmulatorView. */
        view.attachSession(session);

        /* That's all you have to do!  The EmulatorView will call the attached
           TermSession's initializeEmulator() automatically, once it can
           calculate the appropriate screen size for the terminal emulator. */
    }

    /**
     * Create a TermSession connected to a local shell.
     */
    private TermSession createLocalTermSession() {
        /* Instantiate the TermSession ... */
        TermSession session = new TermSession();

        EchoProcess exec;
        try {
            exec = new EchoProcess();
            new Thread(exec).start();
        } catch (Exception e) {
           Log.e(TAG, "Could not start terminal process.", e);
            return null;
        }

        /* ... and connect the process's I/O streams to the TermSession. */
        session.setTermIn(exec.getInputStream());
        session.setTermOut(exec.getOutputStream());

        /* You're done! */
        return session;

    }
}
