package org.y_lab.adapter.in.servlets.support;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DelegatingServletInputStream extends ServletInputStream {
    InputStream inputStream;
    public DelegatingServletInputStream(byte[] value) {
        this.inputStream = new ByteArrayInputStream(value);
    }

    @Override
    public boolean isFinished() {
        try {
            return inputStream.available() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.inputStream.close();
    }
}
