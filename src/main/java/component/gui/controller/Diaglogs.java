package component.gui.controller;

import javax.swing.*;

public class Diaglogs {

    public static boolean AskSaving() {
        int result = JOptionPane.showConfirmDialog(null, "Exist unsaved content, do you want to save?",
                "Confirm Saving", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        switch (result) {
            case JOptionPane.YES_OPTION:
                FileIO.SaveMuiFile();
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
                return false;
        }
        return true;
    }



}

