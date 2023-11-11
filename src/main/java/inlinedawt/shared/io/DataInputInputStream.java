package inlinedawt.shared.io;

import java.io.DataInput;
import java.io.FilterInputStream;
import java.io.IOException;
import lombok.Getter;

public class DataInputInputStream extends FilterInputStream {

    @Getter
    protected DataInput dataInput;

    public DataInputInputStream(DataInput dataInput) {
        super(null);
        this.dataInput = dataInput;
    }

    @Override
    public int read() throws IOException {
        return dataInput.readUnsignedByte();
    }

    @Override
    public int read(byte[] b) throws IOException {
        dataInput.readFully(b);
        return b.length;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        dataInput.readFully(b, off, len);
        return len;
    }

    @Override
    public long skip(long n) throws IOException {
        return dataInput.skipBytes((int) n);
    }

    @Override
    public int available() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void mark(int readlimit) {
    }

    @Override
    public void reset() throws IOException {
    }

    @Override
    public boolean markSupported() {
        return false;
    }

}
