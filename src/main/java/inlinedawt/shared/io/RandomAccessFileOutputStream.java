package inlinedawt.shared.io;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileOutputStream extends DataOutputOutputStream {

    protected RandomAccessFile dataOutput;

    protected boolean forwardExtras;

    public RandomAccessFileOutputStream(RandomAccessFile raf) {
        this(raf, true);
    }

    /**
     * 
     * @param forwardExtras
     *            forward operations such as flush and close, ignore them otherwise.
     */
    public RandomAccessFileOutputStream(RandomAccessFile raf, boolean forwardExtras) {
        super(raf);
        this.dataOutput = raf;
        this.forwardExtras = forwardExtras;
    }

    @Override
    public void flush() throws IOException {
        if (forwardExtras) {
            dataOutput.getFD().sync();
        }
    }

    @Override
    public void close() throws IOException {
        if (forwardExtras) {
            dataOutput.close();
        }
    }

}
