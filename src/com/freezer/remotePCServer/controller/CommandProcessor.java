package com.freezer.remotePCServer.controller;

import java.awt.*;

public class CommandProcessor {
    String message;
    String command;
    String commandData;
    MouseKeyboardController mouseKeyboardController = new MouseKeyboardController();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = (int) screenSize.getWidth();
    int screenHeight = (int) screenSize.getHeight();

    public CommandProcessor() {
    }

    public void parseCommand(String message){
        if(message.equals("TEST_CONNECTION"))
            return;;
        this.message = message;
        if(message.indexOf(':') != -1) {
            this.command = message.substring(0, message.indexOf(':'));
            this.commandData = message.substring(message.indexOf(':') + 1, message.indexOf("|"));
        }
        else {
            this.command = message.substring(0, message.indexOf("|"));
            this.commandData = "";
        }
        System.out.println(command);
        System.out.println(commandData);
    }

    public void implementCommand(){
        int keyCode;
        if (message != null) {
            switch (command) {
                case "LEFT_CLICK":
                    mouseKeyboardController.leftClick();
                    break;
                case "RIGHT_CLICK":
                    mouseKeyboardController.rightClick();
                    break;
                case "DOUBLE_CLICK":
                    mouseKeyboardController.doubleClick();
                    break;
                case "MOUSE_WHEEL":
                    int scrollAmount = Integer.parseInt(commandData);
                    mouseKeyboardController.mouseWheel(scrollAmount);
                    break;
                case "MOUSE_MOVE":
                    int x = Integer.parseInt(commandData.substring(0, commandData.indexOf(',')));
                    int y = Integer.parseInt(commandData.substring(commandData.indexOf(',') + 1));
                    Point point = MouseInfo.getPointerInfo().getLocation();
                    // Get current mouse position
                    float nowx = point.x;
                    float nowy = point.y;
                    mouseKeyboardController.mouseMove((int) (nowx + x), (int) (nowy + y));
                    break;
                case "MOUSE_MOVE_LIVE":
                    // need to adjust coordinates
                    float xCord = Float.parseFloat(commandData.substring(0, commandData.indexOf(',')));
                    float yCord = Float.parseFloat(commandData.substring(commandData.indexOf(',') + 1));
                    xCord = xCord * screenWidth;
                    yCord = yCord * screenHeight;
                    mouseKeyboardController.mouseMove((int) xCord, (int) yCord);
                    break;
                case "KEY_PRESS":
                    keyCode = Integer.parseInt(commandData);
                    mouseKeyboardController.keyPress(keyCode);
                    break;
                case "KEY_RELEASE":
                    keyCode = Integer.parseInt(commandData);
                    mouseKeyboardController.keyRelease(keyCode);
                    break;
                case "CTRL_ALT_T":
                    mouseKeyboardController.ctrlAltT();
                    break;
                case "CTRL_SHIFT_Z":
                    mouseKeyboardController.ctrlShiftZ();
                    break;
                case "ALT_F4":
                    mouseKeyboardController.altF4();
                    break;
                case "TYPE_CHARACTER":
                    char ch = commandData.charAt(0);
                    mouseKeyboardController.typeCharacter(ch);
                    break;
                case "TYPE_KEY":
                    keyCode = Integer.parseInt(commandData);
                    mouseKeyboardController.typeCharacter(keyCode);
                    break;
                case "LEFT_ARROW_KEY":
                    mouseKeyboardController.pressLeftArrowKey();
                    break;
                case "DOWN_ARROW_KEY":
                    mouseKeyboardController.pressDownArrowKey();
                    break;
                case "RIGHT_ARROW_KEY":
                    mouseKeyboardController.pressRightArrowKey();
                    break;
                case "UP_ARROW_KEY":
                    mouseKeyboardController.pressUpArrowKey();
                    break;
                case "F5_KEY":
                    mouseKeyboardController.pressF5Key();
                    break;
            }
        }

    }

}
