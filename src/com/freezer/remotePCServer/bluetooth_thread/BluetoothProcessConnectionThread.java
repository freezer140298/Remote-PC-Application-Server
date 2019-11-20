package com.freezer.remotePCServer.bluetooth_thread;

import com.freezer.remotePCServer.controller.CommandProcessor;

import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;


public class BluetoothProcessConnectionThread implements Runnable {
    private StreamConnection streamConnection;


    public BluetoothProcessConnectionThread(StreamConnection streamConnection) {
        this.streamConnection = streamConnection;
        System.out.println("Bluetooth incoming connection");
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = streamConnection.openDataInputStream();
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
                        Thread.currentThread().interrupt();
                        return;
                    }
                    try {
                        commandProcessor.parseCommand(incomingCommand);
                        commandProcessor.implementCommand();
                    } catch(StringIndexOutOfBoundsException e) {
                        System.out.println("ERROR AT (IndexOutOfBounds): " + incomingCommand);
                        System.out.println(incomingCommand);
                    } catch(NumberFormatException e2) {
                        System.out.println("ERROR AT (NumberFormat): " + incomingCommand);
                        System.out.println(incomingCommand);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeThread() throws IOException {
        streamConnection.close();
        Thread.currentThread().interrupt();
    }
}
