package com.freezer.remotePCServer;

import com.freezer.remotePCServer.bluetooth_thread.BluetoothWaitThread;
import com.freezer.remotePCServer.socket_thread.SocketWaitThread;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import static javax.swing.JOptionPane.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.UnknownHostException;


public class MainGUI {
    private MutableBoolean isBTWaiting = new MutableBoolean(false);
    private MutableBoolean isBTConnected = new MutableBoolean(false);
    private MutableBoolean isWFWaiting = new MutableBoolean(false);
    private MutableBoolean isWFConnected = new MutableBoolean(false);

    private JFrame jFrame;
    private JLabel ipAddrLabel;
    private JPanel mainJPanel;
    private JLabel statusLabel;
    private JCheckBox bluetoothCheckBox;
    private JCheckBox wifiCheckBox;
    private JTextField portTextField;
    private Thread bluetoothWaitThread;
    private BluetoothWaitThread bluetoothWaitThreadObject;
    private Thread socketWaitThread;
    private SocketWaitThread socketWaitThreadObject;
    private Inet4Address inet4Address;

    private int socketPort;

    private MainGUI() {
        try {
            inet4Address = (Inet4Address) Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            ipAddrLabel.setText("Unknown server");
        }

        bluetoothCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!bluetoothCheckBox.isSelected()) {
                    bluetoothWaitThreadObject.closeWaitThread();
                }
                else
                {
                    if(!isBTWaiting.getValue()) {
                        bluetoothWaitThreadObject = new BluetoothWaitThread(statusLabel, isBTWaiting, isBTConnected);
                        bluetoothWaitThread = new Thread(bluetoothWaitThreadObject);
                        bluetoothWaitThread.start();
                        System.out.println("Thread "  + bluetoothWaitThreadObject.getCurrentThreadId() + " has started");
                        statusLabel.setText("Status : Waiting for bluetooth connection...");
                        isBTWaiting.setTrue();
                    }
                }
                if(wifiCheckBox.isSelected())
                {
                    wifiCheckBox.setSelected(false);
                    socketWaitThreadObject.closeWaitThread();
                }
            }
        });

        wifiCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!wifiCheckBox.isSelected()) {
                    socketWaitThreadObject.closeWaitThread();
                }
                else
                {
                    if(!isNumeric(portTextField.getText())) {
                        showMessageDialog(null, "Error", "Port must be digit", ERROR_MESSAGE);
                        wifiCheckBox.setSelected(false);
                        return;
                    }
                    if(!isWFWaiting.getValue()) {
                        socketWaitThreadObject = new SocketWaitThread(Integer.parseInt(portTextField.getText()),statusLabel, isWFWaiting, isWFConnected);
                        socketWaitThread = new Thread(socketWaitThreadObject);
                        socketWaitThread.start();
                        System.out.println("Thread "  + socketWaitThreadObject.getCurrentThreadId() + " has started");
                        statusLabel.setText("Status : Waiting for socket connection...");
                        isWFWaiting.setTrue();
                    }
                }
                if(bluetoothCheckBox.isSelected())
                {
                    bluetoothCheckBox.setSelected(false);
                    bluetoothWaitThreadObject.closeWaitThread();
                }
            }
        });
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if(isNumeric(portTextField.getText())) {
                    setServerLabel(portTextField.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if(isNumeric(portTextField.getText())) {
                    setServerLabel(portTextField.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                if(isNumeric(portTextField.getText())) {
                    setServerLabel(portTextField.getText());
                }
            }
        };

        portTextField.getDocument().addDocumentListener(documentListener);
        setServerLabel("0");
    }

    private void setServerLabel(String port){
        ipAddrLabel.setText("<html><p>Server at:</p><p></p><p>" + inet4Address.getHostAddress() + ":" + port + "</p></html>");
    }


    public static void main(String[] Args) {
        MainGUI mainGUI = new MainGUI();
        mainGUI.jFrame = new JFrame("Remote PC Server");
        mainGUI.jFrame.setContentPane(mainGUI.mainJPanel);
        mainGUI.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainGUI.jFrame.pack();
        mainGUI.jFrame.setResizable(false);
        mainGUI.jFrame.setSize(415,210);
        mainGUI.jFrame.setVisible(true);

    }

    private static boolean isNumeric(final String str) {
        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}
