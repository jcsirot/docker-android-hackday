package com.docker.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import cli.Cli;

/**
 * Created by jcsirot on 18/12/2017.
 */

public class EchoProcess implements Runnable {

    private PipedOutputStream out;
    private PipedInputStream inFromOut;
    private PipedInputStream in;
    private PipedOutputStream outFromIn;

    public EchoProcess() throws IOException {
        out = new PipedOutputStream();
        inFromOut = new PipedInputStream(out);
        in = new PipedInputStream();
        outFromIn = new PipedOutputStream(in);
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1024];
            while (true) {
                int n = inFromOut.read(buf);
                if (n <= 0) {
                    return;
                }
                String s = new String(buf, StandardCharsets.UTF_8);
                String greetings = Cli.greetings(s);
                outFromIn.write(greetings.getBytes(StandardCharsets.UTF_8));
                outFromIn.flush();
            }
        } catch (IOException ioe) {
            // Log some stuff... FIXME
        }
    }

    public InputStream getInputStream() {
        return in;
    }

    public OutputStream getOutputStream() {
        return out;
    }
}
