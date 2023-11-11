package inlinedawt.shared.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileUtils {

    private static final int BUF_SIZE = 32 * 1024;

    /**
     * @param forwardExtras
     *            forward operations such as flush and close, ignore them otherwise.
     */
    public static DataOutputStream newBufferedDataOutputStream(RandomAccessFile raf, boolean forwardExtras) {
        RandomAccessFileOutputStream rafos = new RandomAccessFileOutputStream(raf, forwardExtras);
        BufferedOutputStream bos = new BufferedOutputStream(rafos, BUF_SIZE);
        DataOutputStream dos = new DataOutputStream(bos);
        return dos;
    }

    public static DataInputStream newBufferedDataInputStream(RandomAccessFile raf, boolean forwardExtras) {
        RandomAccessFileInputStream rafis = new RandomAccessFileInputStream(raf, forwardExtras);
        BufferedInputStream bis = new BufferedInputStream(rafis, BUF_SIZE);
        DataInputStream dis = new DataInputStream(bis);
        return dis;
    }

}
