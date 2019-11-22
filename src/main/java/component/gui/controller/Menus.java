package component.gui.controller;

import component.gui.view.MainWindow;
import entity.gui.Status;

public class Menus {

    public static void Init(){

        MainWindow.getInstance().newEmptyMenuItem.addActionListener(e -> {
            switch (Status.GetCurrentStatus()){
                case EDITED:
                    if(!Diaglogs.AskSaving())
                        break;
                case NEW_TEMPLATE:
                    Status.SetCurrentStatus(Status.NEW_EMPTY);
                    MainWindow.getInstance().inputTextPane.setText("");
                    break;
                default:
                    break;
            }
        });

        MainWindow.getInstance().newTemplateMenuItem.addActionListener(e -> {

        });


        MainWindow.getInstance().openMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().saveMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().saveAsMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().exportMidiMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().exitMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().loadSoundFontMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().playDirectMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().stopDirectMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().setNoteMappingMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().transposerMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().instruMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().tipsMenuItem.addActionListener(e -> {

        });

        MainWindow.getInstance().aboutMenuItem.addActionListener(e -> {

        });

    }

}
