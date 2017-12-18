package com.docker.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Created by jcsirot on 18/12/2017.
 */

public class EchoProcess implements Runnable {

    private PipedOutputStream out;
    private PipedInputStream in;

    public EchoProcess() throws IOException {
        out = new PipedOutputStream();
        in = new PipedInputStream(out);
    }

    @Override
    public void run() {
    }

    public InputStream getInputStream() {
        return in;
    }

    public OutputStream getOutputStream() {
        return out;
    }
}
