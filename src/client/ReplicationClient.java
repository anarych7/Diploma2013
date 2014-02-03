package client;

import client.gui.ClientFrame;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

import javax.swing.*;

public class ReplicationClient {

    public static void main(String[] args) throws
            UnsupportedLookAndFeelException,
            IllegalAccessException,
            ClassNotFoundException,
            InstantiationException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        ClientFrame clientFrame = new ClientFrame();
        clientFrame.init();
    }
}
