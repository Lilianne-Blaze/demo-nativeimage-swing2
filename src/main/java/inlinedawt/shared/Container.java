package inlinedawt.shared;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Container {

    private static final int EXTRA_PROPS_SIZE = 1024;

    private String name;

    private int version;

    private long reservedSpace;

    private long createdMillis;

    private long usedSpace = -1;

    private long modifiedMillis;

    private byte[] extraProps = new byte[EXTRA_PROPS_SIZE];

    private byte[] data = null;

    public void setData(byte[] newData) {
        this.data = newData;
        usedSpace = newData.length;
        modifiedMillis = System.currentTimeMillis();
    }

    public void setExtraProps(byte[] extraProps) {
        byte[] newExtraProps = new byte[EXTRA_PROPS_SIZE];
        int len = extraProps.length > EXTRA_PROPS_SIZE ? extraProps.length : EXTRA_PROPS_SIZE;
        System.arraycopy(extraProps, 0, newExtraProps, 0, len);
        this.extraProps = newExtraProps;
    }

}
