package inlinedawt.buildtime;

import inlinedawt.shared.Container;
import inlinedawt.shared.ContainerIO;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ContainerBTUtils {

    private static CharArrayWriter containerList = new CharArrayWriter();

    private static PrintWriter containerListWriter = new PrintWriter(containerList);

    public static void addEmptyContainer(String name, long size) throws IOException {
        String resPath = "containers/" + name + ".container";

        Container c = ContainerIO.newEmpty(name, size);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        ContainerIO.write(dos, c);

        ResourceBTUtils.addResource(resPath, baos.toByteArray());

        containerListWriter.println(name);
        containerListWriter.flush();
    }

    public static void addContainerList() {
        containerListWriter.flush();
        byte[] bytes = containerList.toString().getBytes(StandardCharsets.UTF_8);
        ResourceBTUtils.addResource("containers/list.txt", bytes);
        containerList.reset();
    }

}
