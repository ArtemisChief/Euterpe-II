import com.alee.laf.WebLookAndFeel;
import com.alee.skin.dark.DarkSkin;
import gui.view.MainWindow;
import midipaser.component.MidiParser;

import java.io.File;

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

        WebLookAndFeel.install(DarkSkin.class);

        MainWindow.GetInstance().init().setVisible(true);

        MidiParser.GetInstance().parse(new File("Heartache.mid"));
        
    }

}