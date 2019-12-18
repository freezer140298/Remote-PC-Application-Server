package com.freezer.remotePCServer.socket_thread;

import com.freezer.remotePCServer.controller.CommandProcessor;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;

public class SocketProcessConnectionThread implements Runnable {
    private DataInputStream inputStream;
    private JLabel statusLabel;

    public SocketProcessConnectionThread(DataInputStream inputStream, JLabel statusLabel) {
        this.inputStream = inputStream;
        this.statusLabel = statusLabel;
        System.out.println("Incoming connection");
    }

    @Override
    public void run() {
        //BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        CommandProcessor commandProcessor = new CommandProcessor();
        while(true) {
            byte buffer[] = new byte[24];
            int bytesRead;
            try {
                bytesRead = inputStream.read(buffer);
                String incomingCommand = new String(buffer, 0, bytesRead);
                if(incomingCommand.contains("EXIT_CMD"))
                {
                    System.out.println("Exit from current thread");
                    inputStream.close();

                    statusLabel.setText("Status : Connection closed");
                    Thread.currentThread().interrupt();
                    return;
                }
                try {
                    commandProcessor.parseCommand(incomingCommand);
                    commandProcessor.implementCommand();
                } catch(StringIndexOutOfBoundsException e) {
                    System.out.println("(IndexOutOfBounds) ERROR AT : " + incomingCommand);
                    System.out.println(incomingCommand);
                } catch(NumberFormatException e2) {
                    System.out.println("(NumberFormat) ERROR AT : " + incomingCommand);
                    System.out.println(incomingCommand);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void closeThread() throws IOException {
        inputStream.close();
        Thread.currentThread().interrupt();
    }
}
