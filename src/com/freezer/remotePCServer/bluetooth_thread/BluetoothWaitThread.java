package com.freezer.remotePCServer.bluetooth_thread;

import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.*;
import java.io.IOException;


public class BluetoothWaitThread implements Runnable, WaitThread {
    private JLabel statusLabel;
    private MutableBoolean isBTWaiting;
    private MutableBoolean isBTConnected;

    BluetoothProcessConnectionThread processThread;

    public BluetoothWaitThread(JLabel statusLabel, MutableBoolean isBTWaiting, MutableBoolean isBTConnected) {
        this.statusLabel = statusLabel;
        this.isBTWaiting = isBTWaiting;
        this.isBTConnected = isBTConnected;
    }

    @Override
    public void run() {
        waitForConnection();
    }

    private void waitForConnection(){
        LocalDevice local = null;

        StreamConnectionNotifier notifier;
        StreamConnection connection = null;
        try{
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID(80087355);
            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Status : Error while establishing bluetooth");
            return;
        }

        try {
            System.out.println("Waiting for connection ");
            connection = notifier.acceptAndOpen();
            local.setDiscoverable(DiscoveryAgent.NOT_DISCOVERABLE);

            statusLabel.setText("Status : Bluetooth connection was established");
            processThread = new BluetoothProcessConnectionThread(connection);

            isBTConnected.setTrue();
            isBTWaiting.setFalse();

            processThread.run();
        } catch (IOException e) {
            e.printStackTrace();
            closeWaitThread();
        }
    }

    @Override
    public void closeWaitThread(){
        statusLabel.setText("Status : Ready");
        isBTConnected.setFalse();
        isBTWaiting.setFalse();
        System.out.println(Thread.currentThread().getId() + " interrupted");
        Thread.currentThread().interrupt();
    }

    @Override
    public void printCurrentThreadId() {
        System.out.println(Thread.currentThread().getId());
    }
}
