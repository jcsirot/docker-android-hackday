package com.docker.android;

import java.io.ByteArrayOutputStream;
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            while (true) {
                int n = inFromOut.read(buf);
                if (n <= 0) {
                    return;
                }
                for (int i = 0; i < n; i++) {
                  if (buf[i] == '\n' ||  buf[i] == '\r') {
                    outFromIn.write('\r');
                    outFromIn.write('\n');
                    outFromIn.flush();
                    String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                    baos.reset();
                    String greetings = Cli.version();
                    outFromIn.write(greetings.getBytes(StandardCharsets.UTF_8));
                    outFromIn.flush();
                  } else {
                    baos.write(buf[i]);
                    outFromIn.write(buf[i]);
                    outFromIn.flush();
                  }
                }
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
