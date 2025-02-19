package com.freezer.remotePCServer.socket_thread;

import com.freezer.remotePCServer.WaitThread;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketWaitThread implements Runnable, WaitThread {
    private JLabel statusLabel;
    private MutableBoolean isWFWaiting;
    private MutableBoolean isWFConnected;
    private int port;
    private ServerSocket serverSocket = null;

    private SocketProcessConnectionThread processThread;

    public SocketWaitThread(int port,JLabel statusLabel, MutableBoolean isWFWaiting, MutableBoolean isWFConnected) {
        this.port = port;
        this.statusLabel = statusLabel;
        this.isWFWaiting = isWFWaiting;
        this.isWFConnected = isWFConnected;
    }

    @Override
    public void run() {
        waitForConnection();
    }

    private void waitForConnection(){
        BufferedReader bufferedReader;
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Server socket listen at port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Status : Error while opening socket");
            return;
        }

        try {
            Socket server = serverSocket.accept();
            statusLabel.setText("Status : Socket connection was established");
            DataInputStream inputStream = new DataInputStream(server.getInputStream());
            System.out.println(server.getRemoteSocketAddress());
            processThread = new SocketProcessConnectionThread(inputStream, statusLabel);
            processThread.run();
        } catch (IOException e) {
            e.printStackTrace();
            closeWaitThread();
        }
    }

    @Override
    public void closeWaitThread() {
        statusLabel.setText("Status : Ready");
        isWFConnected.setFalse();
        isWFWaiting.setFalse();
        System.out.println(Thread.currentThread().getId() + " has been interrupted");
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }

    @Override
    public long getCurrentThreadId() {
        return Thread.currentThread().getId();
    }
}
