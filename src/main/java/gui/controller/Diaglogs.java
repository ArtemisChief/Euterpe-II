package gui.controller;

import gui.view.MainWindow;

import javax.swing.*;

public class Diaglogs {

    private static final Diaglogs instance = new Diaglogs();

    public static Diaglogs GetInstance() {
        return instance;
    }

    private final MainWindow mainWindow;

    private Diaglogs() {
        mainWindow = MainWindow.GetInstance();
    }

    public boolean askSaving() {
        int result = JOptionPane.showConfirmDialog(mainWindow, "Exist unsaved content, do you want to save?",
                "Confirm Saving", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        switch (result) {
            case JOptionPane.YES_OPTION:
                FileIO.GetInstance().saveMuiFile();
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
                return false;
        }
        return true;
    }

    public void showErrorInfo(String str) {
        JOptionPane.showMessageDialog(mainWindow, str, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
