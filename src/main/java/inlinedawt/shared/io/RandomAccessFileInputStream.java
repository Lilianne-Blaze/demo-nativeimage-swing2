package inlinedawt.shared.io;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileInputStream extends DataInputInputStream {

    protected RandomAccessFile dataInput;

    protected boolean forwardExtras;

    protected long streamOffset;

    protected long markOffset;

    public RandomAccessFileInputStream(RandomAccessFile raf) {
        this(raf, true);
    }

    /**
     * 
     * @param forwardExtras
     *            forward operations such as mark and reset, ignore them otherwise.
     */
    public RandomAccessFileInputStream(RandomAccessFile raf, boolean forwardExtras) {
        super(raf);
        this.dataInput = raf;
        this.forwardExtras = forwardExtras;
        try {
            this.streamOffset = raf.getFilePointer();
            this.markOffset = streamOffset;
        } catch (IOException e) {
            throw new IllegalStateException("IOException calling getFilePointer, shouldn't happen.", e);
        }
    }

    @Override
    public int available() throws IOException {
        long curPos = dataInput.getFilePointer();
        long len = dataInput.length();
        long remaining = len - curPos;
        if (remaining < Integer.MAX_VALUE) {
            return (int) remaining;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public void close() throws IOException {
        if (forwardExtras) {
            dataInput.close();
        }
    }

    @Override
    public void mark(int readlimit) {
        if (forwardExtras) {
            try {
                markOffset = dataInput.getFilePointer();
            } catch (IOException e) {
                throw new IllegalStateException("IOException calling getFilePointer, shouldn't happen.", e);
            }
        }
    }

    @Override
    public void reset() throws IOException {
        if (forwardExtras) {
            dataInput.seek(markOffset);
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}
