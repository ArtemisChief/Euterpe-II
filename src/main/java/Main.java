import com.alee.laf.WebLookAndFeel;
import com.alee.skin.dark.DarkSkin;
import component.gui.view.MainWindow;

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
        
    }

}