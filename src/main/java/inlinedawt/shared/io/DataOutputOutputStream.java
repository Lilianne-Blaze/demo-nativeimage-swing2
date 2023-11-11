package inlinedawt.shared.io;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import lombok.Getter;

public class DataOutputOutputStream extends FilterOutputStream {

    @Getter
    protected DataOutput dataOutput;

    public DataOutputOutputStream(DataOutput dataOutput) {
        super(null);
        this.dataOutput = dataOutput;
    }

    @Override
    public void write(int b) throws IOException {
        dataOutput.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        dataOutput.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        dataOutput.write(b, off, len);
    }

    /**
     * Do nothing.
     */
    @Override
    public void flush() throws IOException {
    }

    /**
     * Do nothing.
     */
    @Override
    public void close() throws IOException {
    }

}
