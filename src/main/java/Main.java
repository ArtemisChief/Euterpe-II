import com.alee.laf.WebLookAndFeel;
import userInterface.GraphicalUserInterface;

import javax.swing.*;

/**
 * ░░░░░░░░░░░▄▀▄▀▀▀▀▄▀▄░░░░░░░░░░░░░░░░░░
 * ░░░░░░░░░░░█░░░░░░░░▀▄░░░░░░▄░░░░░░░░░░
 * ░░░░░░░░░░█░░▀░░▀░░░░░▀▄▄░░█░█░░░░░░░░░
 * ░░░░░░░░░░█░▄░█▀░▄░░░░░░░▀▀░░█░░░░░░░░░
 * ░░░░░░░░░░█░░▀▀▀▀░░░░░░░░░░░░█░░░░░░░░░
 * ░░░░░░░░░░█░░░░░░░░░░░░░░░░░░█░░░░░░░░░
 * ░░░░░░░░░░█░░░░░░░░░░░░░░░░░░█░░░░░░░░░
 * ░░░░░░░░░░░█░░▄▄░░▄▄▄▄░░▄▄░░█░░░░░░░░░░
 * ░░░░░░░░░░░█░▄▀█░▄▀░░█░▄▀█░▄▀░░░░░░░░░░
 * ░░░░░░░░░░░░▀░░░▀░░░░░▀░░░▀░░░░░░░░░░░░
 */

public class Main {

    public static void main(String[] args) {
//        try {
//            System.setProperty("sun.java2d.noddraw", "true");
//            UIManager.put("RootPane.setupButtonVisible", false);
//            BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
//            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
//            BeautyEyeLNFHelper.launchBeautyEyeLNF();
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }

        WebLookAndFeel.install ();

        GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
        graphicalUserInterface.setVisible(true);
    }

}