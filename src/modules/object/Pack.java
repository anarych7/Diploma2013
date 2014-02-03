package modules.object;

import java.io.ObjectOutputStream;

public class Pack {
    private final ObjectOutputStream objectOutputStream;
    private final NetObject netObject;

    public Pack(ObjectOutputStream objectOutputStream, NetObject netObject) {
        this.objectOutputStream = objectOutputStream;
        this.netObject = netObject;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public NetObject getNetObject() {
        return netObject;
    }

}



