package inlinedawt.shared;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ContainerIO {

    public static Container newEmpty(String name, long reservedSpace) {
        Container c = new Container();
        c.setName(name);
        c.setVersion(1);
        c.setReservedSpace(reservedSpace);
        c.setCreatedMillis(System.currentTimeMillis());
        c.setData(new byte[(int) reservedSpace]);
        return c;
    }

    public static void write(DataOutputStream dos, Container c) throws IOException {
        String sepBlock = ContainerUtils.getSeparator(c);
        dos.write(StringUtils.toAscii(sepBlock));

        dos.writeInt(1); // version

        int reservedSpace = (int) c.getReservedSpace();
        dos.writeLong(c.getReservedSpace());
        dos.writeLong(c.getCreatedMillis());
        int usedSpace = (int) c.getUsedSpace();
        dos.writeLong(usedSpace);
        dos.writeLong(c.getModifiedMillis());

        dos.write(c.getExtraProps());

        if (usedSpace <= 0) {
            dos.write(new byte[reservedSpace]);
        } else {
            byte[] cData = c.getData();
            if (cData.length > usedSpace) {
                throw new IllegalArgumentException("malformed container. data.len > usedSpace.");
            }
            dos.write(cData);
            int fillBytes = reservedSpace - cData.length;
            dos.write(new byte[fillBytes]);
        }

        dos.flush();
    }

    public static Container readResource(String resName) throws IOException {
        String resPath = "containers/" + resName + ".container";

        InputStream is = ContainerIO.class.getResourceAsStream("/" + resPath);

        DataInputStream dis = new DataInputStream(is);
        Container container = read(dis);

        return container;
    }

    public static Container read(DataInputStream dis) throws IOException {
        Container c = new Container();

        byte[] sepBlock = ByteArrayUtils.readNBytes(dis, ContainerUtils.SEP_BLOCK_SIZE);
        String name = ContainerUtils.getNameFromSeparator(StringUtils.fromAscii(sepBlock));

        c.setName(name);

        dis.readInt(); // version, ignore

        c.setReservedSpace(dis.readLong());
        c.setCreatedMillis(dis.readLong());
        c.setUsedSpace(dis.readLong());
        c.setModifiedMillis(dis.readLong());

        byte[] extraProps = ByteArrayUtils.readNBytes(dis, c.getExtraProps().length);
        c.setExtraProps(extraProps);

        byte[] dataBlock = ByteArrayUtils.readNBytes(dis, (int) c.getUsedSpace());
        c.setData(dataBlock);

        int fillerBytes = (int) (c.getReservedSpace() - dataBlock.length);
        if (fillerBytes > 0) {
            ByteArrayUtils.readNBytes(dis, fillerBytes);
        }

        return c;
    }

}
